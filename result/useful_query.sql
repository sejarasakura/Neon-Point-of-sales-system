SELECT * FROM CUSTOMER ORDER BY SUBSTRING(CUSTOMER_ID,3,7);
/*The query how to arrange Customer Table In Order*/
SELECT TOP 1 SUBSTRING(PRODUCT_ID, 3, 6) AS INT FROM PRODUCT ORDER BY PRODUCT_ID DESC;
/*get the last row of table*/
SELECT TOP 1 SUBSTRING(CUSTOMER_ID,3,8) AS INT FROM Customer ORDER BY SUBSTRING(CUSTOMER_ID,3,8) DESC;
/*SELECT THE LARGE ROW NUMBER*/
SELECT TOP 1 SUBSTRING(STAFF_ID,3,8) AS INT FROM STAFF ORDER BY SUBSTRING(STAFF_ID,3,8) DESC;
/*SELECT THE LARGE ROW NUMBER STAFF*/
SELECT * FROM STAFF ORDER BY SUBSTRING(STAFF_ID,3,8);
/* DISPLAY DATA ACCORDING TO ID NUMBER*/
SELECT SUM(P.PRODUCT_QUANTITY*D.UNIT_PRICE) AS TOTAL FROM ORDER_RECORD_DETAIL P,PRODUCT D WHERE D.PRODUCT_ID=P.PRODUCT_ID;
/*Kang yee query 2*/
SELECT O.ORDER_ID FROM ORDER_RECORD O WHERE NOT EXISTS ( SELECT P.ORDER_ID FROM PAYMENT P WHERE P.ORDER_ID = O.ORDER_ID );
/*How to get the order ont yet paid by the customer*/
SELECT product_id,SUM(product_quantity) as sum_of_product FROM Order_record_detail GROUP BY product_id ORDER BY product_id;
/*Use to get the sum of product quantity*/
SELECT Top 10 product_id,SUM(product_quantity) as sum_of_product FROM Order_record_detail GROUP BY product_id ORDER BY sum_of_product DESC;
/*List top 10 higher sold product*/
SELECT Top 10 product_id,SUM(product_quantity) as sum_of_product, SUM(unit_final_price * product_quantity) as total_outcome FROM Order_record_detail GROUP BY product_id ORDER BY sum_of_product DESC;
/*The query get total out come of each product*/
SELECT product_quantity,unit_final_price,(unit_final_price*product_quantity) as final_price, order_id, product_id FROM order_record_detail ORDER BY product_id;
/*Check query the order id order with product*/
SELECT Top 10 O.product_id,SUM(O.product_quantity) as O.sum_of_product, SUM(O.unit_final_price * O.product_quantity) as total_outcome FROM Order_record_detail O GROUP BY O.product_id ORDER BY O.sum_of_product DESC;
/*Error query*/
SELECT * FROM Product P, Order_record_detail O, Payment Pay, Order_record OD WHERE P.product_id = O.product_id AND OD.order_id = Pay.order_id AND OD.order_id = O.order_id AND payment_date BETWEEN #8/1/2019# AND #8/15/2019# ORDER BY payment_date;
/*Check the range of date that in range 4 tabele query*/
SELECT TOP 20 DISTINCT O.product_id,SUM(O.UNIT_FINAL_PRICE * O.PRODUCT_QUANTITY) as total_price,SUM(O.PRODUCT_QUANTITY) as total_product_quantity FROM Order_record_detail O, Payment Pay, Order_record OD WHERE OD.order_id = Pay.order_id AND OD.order_id = O.order_id AND payment_date BETWEEN #8/1/2019# AND #8/15/2019# GROUP BY product_id ORDER BY total_product_quantity DESC;
/*Get the monthly top 10 higher sales product in date of range */
SELECT P.PRODUCT_NAME,D.PRODUCT_QUANTITY,D.UNIT_FINAL_PRICE,D.UNIT_DISCOUNT FROM PRODUCT P, ORDER_RECORD O, ORDER_RECORD_DETAIL D WHERE O.ORDER_ID = D.ORDER_ID AND P.PRODUCT_ID = D.PRODUCT_ID AND O.ORDER_ID = 'OR00000001';
/*Query to find out the product from the particular order*/
SELECT product_id,SUM(product_quantity) as sum_of_product FROM Order_record_detail GROUP BY product_id ORDER BY product_id;
/*Select The Total Order Product*/
SELECT * FROM ORDER_RECORD_DETAIL;
/*Order record detail*/
UPDATE ORDER_RECORD_DETAIL SET ORDER_ID ='OR00000177' WHERE ORDER_ID = 'null';
/*Upadate Order Record*/
