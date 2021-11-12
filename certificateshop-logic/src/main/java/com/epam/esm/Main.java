package com.epam.esm;

import com.epam.esm.model.impl.GiftCertificate;
import com.google.gson.Gson;

import java.time.LocalDateTime;

/**
 * The class is used for generation the scripts and data to fill the database.
 * That is why we need it here as the history of the work for the Module3.
 */
public class Main {
    public static void main(String[] args) {
//
//        for (int i = 303; i < 1313; i++) {
//            int price = 1313 % i;
//            int duration = price * 10 + 5;
//
//            System.out.println("insert into gift_certificate (name, description, price, duration, create_date, last_update_date) values" +
//                    " ('certifiсate" + i +"'"+ ", " + "'description" + i +"'"+ ", " + price + ", " + duration + ", " + "now()" + ", " + "now()" + ");");
//        }


//                for (int i = 10; i < 1010; i++) {
//                    System.out.println("INSERT INTO user (nickName) VALUES ("+ "'user" + i + "'" + ");");
//                }


//                for (int i = 304; i < 1313; i++) {
//                    System.out.println("INSERT INTO tag (name) VALUES ('tag" + i + "');");
//                }
// has_tag table
//        for (int i = 48; i < 1058; i++) {
//            int j = (int)(Math.random() * 1008) + 40;
//            System.out.println("insert into has_tag (certificateId, tagId) values (" + i + " ,"+  j+ ");");
//        }

//        for (int i = 40; i < 1049; i++) {
//            int j = (int)(Math.random() * 1009) + 48;
//            System.out.println("insert into has_tag (certificateId, tagId) values (" + j + " ,"+  i+ ");");
//        }
// userorder table
//        for (int i = 14; i < 1014; i++) {
//            int userNumber = i - 4;
//            String orderName = "Order_of_User" + userNumber + "_" + LocalDateTime.now();
//            System.out.println("insert into userorder (userId, create_date, name) values (" + i + ", " + "now()" + ", " + "'" + orderName.toString() + "'" + ");");
//        }

//       the custom generator in 'userorder_certificate' table, der soll irgendwo beim OrderService Klasse benutzt wird.
////        for (int i = 381; i < 1023; i++) {
////            int j = (int) (Math.random() * 1008) + 40;
////            GiftCertificate certificate = certificateDAO.findById(j).get();
////            Gson gson = new Gson();
////            String certificateInJson = gson.toJson(certificate);
////            orderDao.saveIdsInUserorder_certificateTable(i, certificate.getId(), certificate, certificate.getPrice());
////        }
    }
}
