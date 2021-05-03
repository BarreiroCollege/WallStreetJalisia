/*
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package javafx.scene.shape;

/**
Builder class for javafx.scene.shape.Ellipse
@see javafx.scene.shape.Ellipse
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class EllipseBuilder<B extends javafx.scene.shape.EllipseBuilder<B>> extends javafx.scene.shape.ShapeBuilder<B> implements javafx.util.Builder<javafx.scene.shape.Ellipse> {
    protected EllipseBuilder() {
    }

    /** Creates a new instance of EllipseBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.shape.EllipseBuilder<?> create() {
        return new javafx.scene.shape.EllipseBuilder();
    }

    private int __set;
    public void applyTo(javafx.scene.shape.Ellipse x) {
        super.applyTo(x);
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setCenterX(this.centerX);
        if ((set & (1 << 1)) != 0) x.setCenterY(this.centerY);
        if ((set & (1 << 2)) != 0) x.setRadiusX(this.radiusX);
        if ((set & (1 << 3)) != 0) x.setRadiusY(this.radiusY);
    }

    private double centerX;
    /**
    Set the value of the {@link javafx.scene.shape.Ellipse#getCenterX() centerX} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B centerX(double x) {
        this.centerX = x;
        __set |= 1 << 0;
        return (B) this;
    }

    private double centerY;
    /**
    Set the value of the {@link javafx.scene.shape.Ellipse#getCenterY() centerY} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B centerY(double x) {
        this.centerY = x;
        __set |= 1 << 1;
        return (B) this;
    }

    private double radiusX;
    /**
    Set the value of the {@link javafx.scene.shape.Ellipse#getRadiusX() radiusX} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B radiusX(double x) {
        this.radiusX = x;
        __set |= 1 << 2;
        return (B) this;
    }

    private double radiusY;
    /**
    Set the value of the {@link javafx.scene.shape.Ellipse#getRadiusY() radiusY} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B radiusY(double x) {
        this.radiusY = x;
        __set |= 1 << 3;
        return (B) this;
    }

    /**
    Make an instance of {@link javafx.scene.shape.Ellipse} based on the properties set on this builder.
    */
    public javafx.scene.shape.Ellipse build() {
        javafx.scene.shape.Ellipse x = new javafx.scene.shape.Ellipse();
        applyTo(x);
        return x;
    }
}