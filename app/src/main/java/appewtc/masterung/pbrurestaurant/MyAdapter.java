package appewtc.masterung.pbrurestaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by masterUNG on 6/12/15 AD.
 */
public class MyAdapter extends BaseAdapter{

    //Explicit
    private Context myContext;
    private String[] foodStrings, priceStrings;
    private int[] intImageFood;

    public MyAdapter(Context myContext, String[] foodStrings, String[] priceStrings, int[] intImageFood) {
        this.myContext = myContext;
        this.foodStrings = foodStrings;
        this.priceStrings = priceStrings;
        this.intImageFood = intImageFood;
    }   // Constructor

    @Override
    public int getCount() {
        return foodStrings.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater objLayoutInflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View objView1 = objLayoutInflater.inflate(R.layout.listview_rom, viewGroup, false);

        //Show Food
        TextView listFood = (TextView) objView1.findViewById(R.id.txtListFood);
        listFood.setText(foodStrings[i]);

        //Show Price
        TextView listPrice = (TextView) objView1.findViewById(R.id.txtListPrice);
        listPrice.setText(priceStrings[i]);

        //Show Image Food
        ImageView listImageFood = (ImageView) objView1.findViewById(R.id.imvListFood);
        listImageFood.setBackgroundResource(intImageFood[i]);

        return objView1;
    }
}   // Main Class
