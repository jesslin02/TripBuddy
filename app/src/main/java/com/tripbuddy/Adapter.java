package com.tripbuddy;

import android.content.Context;

public interface Adapter {
    void deleteItem(int position);
    Context getContext();
}
