package com.github.baoti.pioneer.misc.picasso;

import com.github.baoti.pioneer.entity.ImageBean;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by liuyedong on 2014/12/26.
 */
public class PicassoHelper {
    public static RequestCreator load(Picasso picasso) {
        return picasso.load((String) null);
    }

    public static RequestCreator load(Picasso picasso, ImageBean imageBean) {
        if (imageBean == null) {
            return load(picasso);
        }
        return picasso.load(imageBean.getUrl());
    }
}
