# ShoppingList   [![CircleCI](https://circleci.com/gh/mabrosim/ShoppingList.svg?style=svg)](https://circleci.com/gh/mabrosim/ShoppingList)

Smart Shopping List application with export and share functionality.

### Prepare a list:
Application allows to create, store and manage a vocabulary of items grouped by labels.
To prepare the list - simply tick items and swipe to the "At The Shop" page when ready.

### At The Shop:
The prepared items are shown as checked first and grouped by labels.
Unchecked items stay visible, but crossed out, for 15 minutes.

### Export and Share:
Sharing happens in binary data format via an android SEND intent, 
an email attachment is preferred.

Application items data can be exported into formats:
- Google Protobuf binary (proto schema is in the repository)
- CSV (item names and labels only)
- JSON

### Import formats:
- Google Protobuf (proto schema is in the repository)
- CSV (item names and labels only)

Just create a table in any spreadsheet application (excel, numbers or google sheets),
where items provided in columns and the column heads are the labels aka group names.
Export the sheet as CSV and send it as an attachment to email box on your Android phone.

### Settings:
Google cloud for backup & restored to be implemented
Debug action items to preview the exported data in a popup viewer.


#### The app is availaible on Google Play:

<a href="https://play.google.com/store/apps/details?id=fi.mabrosim.shoppinglist" target="top">
   <img alt="Get it on Google Play"
        src="https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png" width="200"/>
</a>
