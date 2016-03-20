# 原理
圆形头像嘛说到底就是张图片，所以自定义圆形图片控件自然要继承**Android**原生的**ImgaeView**,实现其中的**setImageBitmap**，**setImageDrawable**，**setImageURI**，**setImageResource**的方法。当然主要的**onDraw**方法也不能缺少，在其中要实现圆形头像的绘制。这里还要借助两个主要的类**Matrix**与**BitmapShader**，通过这两个类实现图片的缩放效果。

## BitmapShader解释
**BitmapShader**有三个参数  

* bitmap - The bitmap to use inside the shader
* tileX - The tiling mode for x to draw the bitmap in.
* tileY - The tiling mode for y to draw the bitmap in.

**tileX**与**tileY**都是**Shader.TileMode**类型，有三个选择值

* CLAMP 拉伸 
* PEREAT 重复
* MIRROR 镜像

> *我们这里只要使用CLAMP就可以了，想知道其它的选择值的具体效果可以参考这个链接：http://blog.csdn.net/sjf0115/article/details/7267532*

## 获取缩放值
图片过大与过小都不好，不可能绝对的适中，所以缩放值的获取是最重要的。我们要比较图片宽高与控件宽高，通过错位相乘来比较图片的宽高哪个与控件的宽高相差更大。取相差大的值进行比较得到缩放值。下面是主要实现代码:

```
mDrawableRec.set(0, 0, getWidth(), getHeight());
        if (mBitmapWidth * mDrawableRec.height() > mDrawableRec.width() * mBitmapheight) {
            scal = mDrawableRec.height() / mBitmapheight;
        } else {
            scal = mDrawableRec.width() / mBitmapWidth;
        }
```
### 设置BitmapShader
```
mBitmapShader = new BitmapShader(mBitmap, 	Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
mPaint.setShader(mBitmapShader);
```

### 设置缩放与矩阵
```
mMatrix.setScale(scal, scal);
mBitmapShader.setLocalMatrix(mMatrix);
```
	
## onDraw绘制
```
    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmap == null) {
            return;
        }
        //填充
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mFillPaint);
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mPaint);
        //描边
        if (mStrokeWidth != 0)
        canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, mRadius, mBorderPaint);
    }
```
> *这里的顺序不要打乱，否则回影响图片原有的颜色效果*

好了主要的实现方法就是这些了，其实只要理解上面的就基本上掌握了圆形头像的设置了。

## 对外方法
* setBorderColor(); 设置边界颜色
* setFillColor();	填充颜色
* setStrokeWidth(); 描边宽度

## 引用
使用的话直接在xml文件中使用自己自定义的**CircleImageView**控件，

**注意：**如果要在xml中设置默认显示图片时不要使用background设置，否则可能会出差错，使用src就不会有问题了。
# 效果图
![效果图](https://github.com/idisfkj/CircleImage/raw/master/gif/CircleImageView.gif)

---

个人博客：http://idisfkj.github.io/