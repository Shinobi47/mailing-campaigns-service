INSERT INTO DATA_ASSET(ASSET_ID, ASSET_NAME)
VALUES(1, 'ASSET1');

INSERT INTO DATA_GROUP(GROUP_ID, GROUP_NAME, GROUP_CREAT_DATE, GROUP_ASSET_ID)
VALUES(1, 'GROUP1', null, 1);

INSERT INTO DATA_GROUP(GROUP_ID, GROUP_NAME, GROUP_CREAT_DATE, GROUP_ASSET_ID)
VALUES(2, 'GROUP2', null, 1);


INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(1, 'mail1@isp1.com', 'isp1', 1);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(2, 'mail1@isp2.com', 'isp2.com', 1);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(3, 'mail1@isp3.com', 'isp3', 1);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(4, 'mail1@isp4.com', 'isp4', 1);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(5, 'mail1@isp5.com', 'isp5', 1);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(6, 'mail1@isp6.com', 'isp6', 2);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(7, 'mail1@isp7.com', 'isp7', 2);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(8, 'mail1@isp8.com', 'isp8', 2);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(9, 'mail1@isp9.com', 'isp9', 2);
INSERT INTO DATA_ITEM(ITEM_ID, PROSPECT_EMAIL, EMAIL_ISP, ITEM_GROUP_ID)
VALUES(10, 'mail1@isp0.com', 'isp0', 2); 