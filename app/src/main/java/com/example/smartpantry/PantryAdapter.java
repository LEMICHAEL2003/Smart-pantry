package com.example.smartpantry;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.time.LocalDate;
import java.util.List;

public class PantryAdapter extends ArrayAdapter<PantryItem> {
    private final boolean showExpiry;

    public PantryAdapter(Context context, List<PantryItem> items, boolean showExpiry) {
        super(context, 0, items);
        this.showExpiry = showExpiry;
    }

    @Override public View getView(int position, View row, ViewGroup parent) {
        if (row == null) row = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
        PantryItem item = getItem(position);
        ((TextView) row.findViewById(R.id.row_title)).setText(item.name);
        String detail = BaseActivity.amount(item);
        if (!item.expiry.isEmpty()) {
            LocalDate expiry = LocalDate.parse(item.expiry);
            detail += " · " + item.expiry;
            if (expiry.isBefore(LocalDate.now())) detail += " · Expired";
            else if (showExpiry && !expiry.isAfter(LocalDate.now().plusDays(3))) detail += " · Use soon";
        }
        ((TextView) row.findViewById(R.id.row_description)).setText(detail);
        return row;
    }
}
