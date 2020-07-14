package com.suriyal.webservicevollery2insertds.ui.addproduct;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.suriyal.webservicevollery2insertds.R;
import com.suriyal.webservicevollery2insertds.Util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class AddProductFragment extends Fragment {
    private EditText tname, tprice, tquantity;
    private Spinner tcategory;
    private ImageView productImage;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_addproduct, container, false);
        return root;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tname = view.findViewById(R.id.productname);
        tprice = view.findViewById(R.id.productprice);
        tquantity = view.findViewById(R.id.productquantity);
        tcategory = view.findViewById(R.id.productcategory);
        productImage = view.findViewById(R.id.productimage);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu pm = new PopupMenu(getActivity(), productImage);
                pm.getMenuInflater().inflate(R.menu.mymenu, pm.getMenu());

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.camera:
                                openCamera();
                                break;
                            case R.id.gallery:
                                openGallery();
                                break;
                        }
                        return false;
                    }
                });

                pm.show();
            }
        });


        Button button = view.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valid()) {
                    sendToServer();
                }
            }
        });
    }

    private void openCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1001);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1002);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1001) {
                //camera handling code
                cameraHandlingCode(data);
            }

            if (requestCode == 1002) {
                //gallery handling code
                galleryHandlingCode(data);
            }
        }

    }

    private void cameraHandlingCode(Intent data)
    {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        productImage.setImageBitmap(thumbnail);
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte bb[] = bytes.toByteArray();
        imageurl = Base64.encodeToString(bb, Base64.DEFAULT);


    }
    private void galleryHandlingCode(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        productImage.setImageBitmap(thumbnail);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        byte bb[] = bytes.toByteArray();
        imageurl = Base64.encodeToString(bb, Base64.DEFAULT);
    }

    private String name, price, quantity, category, imageurl;

    private boolean valid() {
        name = tname.getText().toString();
        price = tprice.getText().toString();
        quantity = tquantity.getText().toString();
        category = tcategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getActivity(), "please enter name", Toast.LENGTH_SHORT).show();
            tname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(price)) {

            Toast.makeText(getActivity(), "please enter price", Toast.LENGTH_SHORT).show();
            tprice.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(quantity)) {

            Toast.makeText(getActivity(), "please enter quantity", Toast.LENGTH_SHORT).show();
            tquantity.requestFocus();
            return false;
        } else if (category.equalsIgnoreCase("select category")) {

            Toast.makeText(getActivity(), "please select category", Toast.LENGTH_SHORT).show();
            tcategory.requestFocus();
            return false;
        } else if (imageurl == null) {

            Toast.makeText(getActivity(), "please select image", Toast.LENGTH_SHORT).show();
            productImage.requestFocus();
            return false;
        } else {
            return true;
        }

    }

    private void sendToServer() {
        StringRequest sr=new StringRequest(Request.Method.POST, Util.INSERT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String,String> h=new HashMap<>();
                h.put("name",name);
                h.put("price",price);
                h.put("quantity",quantity);
                h.put("category",category);
                h.put("image",imageurl);

                return h;


            }
        };
        Volley.newRequestQueue(getActivity()).add(sr);
    }
}