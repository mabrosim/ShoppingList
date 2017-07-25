package fi.mabrosim.shoppinglist.tasks;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.mabrosim.shoppinglist.BuildConfig;
import fi.mabrosim.shoppinglist.data.records.Item;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MagicSelectionTest {
    private static final String TAG             = "MagicSelectionTest";
    private static final Type   TYPE_ITEMS_JSON = new TypeToken<List<Item>>() {
    }.getType();

    @Test
    public void fileObjectShouldNotBeNull() throws Exception {
        File file = getFileFromPath(this, "items-20170724-1157.json");
        Assert.assertNotNull(file);
    }

    @Test
    public void readItemsFromJsonFile() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        Assert.assertFalse(items.isEmpty());
        Assert.assertEquals(244, items.size());
    }

    @Test
    public void selectItemsByRelevanceOld() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        ArrayList<Item> selectedItems = new ArrayList<>();
        for (Item item : DoTheMagicTask.selectItemsByRelevance(items)) {
            if (item.isChecked()) {
                selectedItems.add(item);
            }
        }
        verifyLogs(887499661697615L, 685794681017504L, selectedItems);
    }

    @Test
    public void selectItemsByRelevanceNew() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        ArrayList<Item> selectedItems = new ArrayList<>();
        for (Item item : DoTheMagicTask.selectItemsByRelevance2(items)) {
            if (item.isChecked()) {
                selectedItems.add(item);
            }
        }
        verifyLogs(1129545110660895L, 907669903690797L, selectedItems);
    }

    @Test
    public void dropShortSelectionsByTimeDeltaTest() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        verifyLogs(4316466399175103L, 4235786704524526L, items);

        DoTheMagicTask.dropShortSelectionsByTimeDelta(items);

        verifyLogs(3711355151683887L, 3630675457033355L, items);
    }

    @Test
    public void reduceLogsTest() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        verifyLogs(4316466399175103L, 4235786704524526L, items);

        DoTheMagicTask.reduceLogs(items);

        verifyLogs(3933229337480154L, 4054253376138406L, items);
    }

    @Test
    public void reduceLogsAndDropSelectionsTest() throws Exception {
        Reader reader = new FileReader(getFileFromPath(this, "items-20170724-1157.json"));
        List<Item> items = new Gson().fromJson(reader, TYPE_ITEMS_JSON);

        verifyLogs(4316466399175103L, 4235786704524526L, items);

        DoTheMagicTask.reduceLogs(items);
        DoTheMagicTask.dropShortSelectionsByTimeDelta(items);

        verifyLogs(3428969848823695L, 3549993887482091L, items);
    }


    private static void verifyLogs(final long checkedSumExp, final long uncheckedSumExp, List<Item> items) {
        Long checkedSum = 0L;
        Long uncheckedSum = 0L;
        for (Item item : items) {
            for (Long c : item.getLogCheckedList()) {
                checkedSum += c;
            }
            for (Long u : item.getLogUncheckedList()) {
                uncheckedSum += u;
            }
        }
        Assert.assertEquals(checkedSumExp, checkedSum.longValue());
        Assert.assertEquals(uncheckedSumExp, uncheckedSum.longValue());
    }

    private static File getFileFromPath(Object obj, String fileName) {
        ClassLoader classLoader = obj.getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        return new File(resource.getPath());
    }

    private static void printItemsSorted(List<Item> items) {
        printItemsSorted("", items);
    }

    private static void printItemsSorted(String msg, List<Item> items) {
        System.out.println(msg + " ============");
        Collections.sort(items, new DoTheMagicTask.ByCheckFrequency());
        for (Item item : items) {
            System.out.println(item.getName() + " " + item.getLogCheckedList());
        }
    }
}
