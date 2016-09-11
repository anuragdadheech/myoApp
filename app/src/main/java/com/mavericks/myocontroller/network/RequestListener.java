package com.mavericks.myocontroller.network;

/**
 * @author Anurag
 */
public interface RequestListener<RESULT> {
    void onRequestFailure(Exception e);
    void onRequestSuccess(RESULT jsonObject);
}
