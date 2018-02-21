package fi.mabrosim.shoppinglist.ui.editors;

import fi.mabrosim.shoppinglist.data.records.ItemList;
import fi.mabrosim.shoppinglist.utils.FileType;
import fi.mabrosim.shoppinglist.utils.FileUtils;

public class AddItemListActivity extends EditItemListActivity {

    public AddItemListActivity() {
        mItemList = new ItemList();
        mItemList.setName(FileUtils.getBase(FileUtils.currentTimeToFileName(FileType.PROTO)));
        mItemList.setCurrent(true);
    }
}
