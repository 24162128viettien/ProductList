package com.example.productlist;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserData {
    private static String uid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static CollectionReference cart() {
        return FirebaseFirestore.getInstance()
                .collection("users").document(uid()).collection("cart");
    }

    public static CollectionReference orders() {
        return FirebaseFirestore.getInstance()
                .collection("users").document(uid()).collection("orders");
    }
}