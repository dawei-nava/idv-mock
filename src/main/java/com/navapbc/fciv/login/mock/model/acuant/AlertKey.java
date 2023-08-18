package com.navapbc.fciv.login.mock.model.acuant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AlertKey {
  TWO_D_BARCODE_READ(
      "2D Barcode Read",
      "This authentication will fail if a 2D barcode is expected but could not be read successfully.  This could be caused by a 2D barcode that is dirty, in poor condition, or is covered by a foreign material such as a sticker.  It is also possible that the 2D barcode could be invalid or a poor quality replication of an authentic barcode.",
      "The 2D barcode was read successfully",
      "Verified that the two-dimensional barcode on the document was read successfully.",
      "The 2D barcode on the could not be read or may be missing.  Confirm that it is present, is not obstructed with any foreign material (e.g., dirt, labels, stickers), and is not excessively cracked or worn."),
  TWO_D_BARCODE_CONTENT(
      "2D Barcode Content",
      "This authentication will fail if a 2D barcode is read but cannot be correctly decoded.  This failure could be caused by if the 2D barcode was incorrectly encoded, either inadvertently due to a manufacturing problem or intentionally.",
      "The 2D barcode is formatted correctly",
      "Checked the contents of the two-dimensional barcode on the document.",
      "Verify that the data extracted from the 2D barcode appears to be correct and matches the data present on the front of the document."),
  BIRTH_DATE_CROSSCHECK(
      "Birth Date Crosscheck",
      "The birth date is present in multiple locations on this document (e.g., visual, 2D barcode, magnetic stripe, contactless smart card).  This test compares this field between two or more of these sources to verify that they match.",
      "The birth dates match",
      "Compare the machine-readable birth date field to the human-readable birth date field.",
      "The birth date field on the document may have been misread due to dirt or wear.  Confirm that the data from all visible sources match and are free of obstruction."),
  BIRTH_DATE_VALID(
      "Birth Date Valid",
      "Verified that the birth date is valid, in the expected format, and occurs on or before the current date and not outside a reasonable range.",
      "The birth date is valid",
      "Verified that the birth date is valid.",
      "The birth date on the document may have been misread.  Confirm that it is legible."),
  DOCUMENT_CLASSIFICATION(
      "Document Classification",
      "Verified that the document is recognized as a supported document type and that the type of document can be fully authenticated.",
      "The document type is supported",
      "Verified that the type of document is supported and is able to be fully authenticated.",
      "This test may fail if a document cannot be successfully classified as a supported document type.  This may occur if the document is fraudulent as some fraudulent documents differ so much from authentic documents that they will not be recognized as that type of document.  This may also occur if a valid document is significantly worn or damaged or if the document is of a new or different type that is not yet supported by the library.  The document should be examined manually."),
  DOCUMENT_CROSSCHECK_AGGREGATION(
      "Document Crosscheck Aggregation",
      "There are multiple sources of data on the document. This test verifies that there are not widespread differences in data between data sources, which can indicate a fake or tampered document.",
      "There are not a large number of differences between electronic and human-readable data sources",
      "Compared the machine-readable fields to the human-readable fields.",
      "Several crosschecks are failing. Confirm that the data from all visible sources match and are free of obstruction."),
  DOCUMENT_EXPIRED(
      "Document Expired",
      "Verified that the document expiration date does not occur before the current date.",
      "The document has not expired",
      "Checked if the document is expired.",
      "The expiration date on the document may have been misread.  Confirm that it is legible and occurs on or after the current date.  Also confirm that the current date and time of the host computer is correctly set."),
  DOCUMENT_NUMBER_CROSSCHECK(
      "Document Number Crosscheck",
      "The document number is present in multiple locations on this document (e.g., visual, 2D barcode, magnetic stripe, contactless smart card).  This test compares this field between two or more of these sources to verify that they match.",
      "The document numbers match",
      "Compare the machine-readable document number field to the human-readable document number field.",
      "The document number field on the document may have been misread due to dirt or wear.  Confirm that the data from all visible sources match and are free of obstructions."),
  EXPIRATION_DATE_CROSSCHECK("Expiration Date Crosscheck"),
  EXPIRATION_DATE_VALID("Expiration Date Valid"),
  FULL_NAME_CROSSCHECK("Full Name Crosscheck"),
  IMAGE_TAMPERING_CHECK("Image Tampering Check"),
  ISSUE_DATE_CROSSCHECK("Issue Date Crosscheck"),
  ISSUE_DATE_VALID("Issue Date Valid"),
  SEX_CROSSCHECK("Sex Crosscheck"),
  ONED_BARCODE("1D Barcode"),
  TWOD_ADDRESS("2D Address"),
  TWOD_ADDRESS_CITY("2D Address City"),
  TWOD_ADDRESS_LINE_1("2D Address Line 1"),
  TWOD_ADDRESS_LINE_2("2D Address Line 2"),
  TWOD_ADDRESS_POSTAL_CODE("2D Address Postal Code"),
  TWOD_ADDRESS_STATE("2D Address State"),
  TWOD_BIRTH_DATE("2D Birth Date"),
  TWOD_DD_NUMBER("2D DD Number"),
  TWOD_DOCUMENT_NUMBER("2D Document Number"),
  TWOD_EXPIRATION_DATE("2D Expiration Date"),
  TWOD_EYE_COLOR("2D Eye Color"),
  TWOD_FIRST_NAME("2D First Name"),
  TWOD_FULL_NAME("2D Full Name"),
  TWOD_GIVEN_NAME("2D Given Name"),
  TWOD_HEIGHT("2D Height"),
  TWOD_ISSUE_DATE("2D Issue Date"),
  TWOD_ISSUING_STATE_CODE("2D Issuing State Code"),
  TWOD_ISSUING_STATE_NAME("2D Issuing State Name"),
  TWOD_SEX("2D Sex"),
  TWOD_SURNAME("2D Surname"),
  DATA_2DBARCODE("Data_2DBarCode"),
  VIZ_ADDRESS("VIZ Address"),
  VIZ_BIRTHDATE("VIZ Birth Date"),
  VIZ_DOCUMENT_NUMBER("VIZ Document Number"),
  VIZ_EXPIRATION_DATE("VIZ Expiration Date"),
  VIZ_EYE_COLOR("VIZ Eye Color"),
  VIZ_FULL_NAME("VIZ Full Name"),
  VIZ_GIVEN_NAME("VIZ Given Name"),
  VIZ_HEIGHT("VIZ Height"),
  VIZ_ISSUE_DATE("VIZ Issue Date"),
  VIZ_PHOTO("VIZ Photo"),
  VIZ_SEX("VIZ Sex"),
  VIZ_SIGNATURE("VIZ Signature"),
  VIZ_SURNAME("VIZ Surname"),
  ADDRESS("Address"),
  ADDRESS_CITY("Address City"),
  ADDRESS_LINE_1("Address Line1"),
  ADDRESS_LINE_2("Address Line2"),
  ADDRESS_POSTAL_CODE("Address Postal Code"),
  ADDRESS_STATE("Address State"),
  BIRTH_DATE("BIRTH DATE"),
  DD_NUMBER("DD Number"),
  DOCUMENT_CLASS_NAME("Document Class Name"),
  DOCUMENT_NUMBER("Document Number"),
  EXPIRATION_DATE("Expiration Date"),
  EYE_COLOR("Eye Color"),
  FIRST_NAME("First Name"),
  FULL_NAME("Full Name"),
  GIVEN_NAME("Given Name"),
  HEIGHT("Height"),
  ISSUE_DATE("Issue Date"),
  ISSUE_STATE_CODE("Issue State Code"),
  ISSUE_STATE_NAME("Issue State Name"),
  PHOTO("Photo"),
  SEX("Sex"),
  SIGNATURE("Signature"),
  SURNAME("Surname"),
  TWOD_BARCODE("2D Barcode"),
  DATE_FONT("Date Font"),
  EYES_LABEL_PATTERN("Eyes Label Pattern"),
  HEADER("Header"),
  HEADER_LEFT("Header Left"),
  HEADER_FONT("Header Font"),
  LOWER_DATA_LABELS("Lower Data Labels"),
  LOWER_DATA_LABELS_RIGHT("Lower Data Labels Right"),
  MICROPRINT("Microprint"),
  VISIBLE_PATTERN("Visible Pattern");

  @JsonValue public final String Key;

  public final String Information;
  public final String Dispostion;

  public final String Description;

  public final String Actions;

  private AlertKey(String key) {
    this(key, null, null, null, null);
  }

  private AlertKey(
      String key, String information, String dispostion, String description, String actions) {
    Key = key;
    Information = information;
    Dispostion = dispostion;
    Description = description;
    Actions = actions;
  }
}
