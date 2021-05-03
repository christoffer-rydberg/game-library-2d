package com.gamelibrary2d.common;

import java.util.Objects;

/**
 * A Rectangle is an immutable representation of an area.
 */
public class Rectangle {

    /**
     * Represents an empty area.
     */
    public static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);

    /**
     * Represents an infinite area.
     */
    public static final Rectangle INFINITE = new Rectangle(
            Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
            Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    private final float lowerX, lowerY, upperX, upperY;

    /**
     * Creates a new rectangle with the specified bounds.
     *
     * @param lowerX The x-coordinate of the lower left corner.
     * @param lowerY The y-coordinate of the lower left corner.
     * @param upperX The x-coordinate of the upper right corner.
     * @param upperY The y-coordinate of the upper right corner.
     */
    public Rectangle(float lowerX, float lowerY, float upperX, float upperY) {
        this.lowerX = lowerX;
        this.lowerY = lowerY;
        this.upperX = upperX;
        this.upperY = upperY;
    }

    /**
     * Creates a new rectangle of the specified size with center at origin (0, 0).
     *
     * @param size The width and height of the rectangle.
     */
    public static Rectangle create(float size) {
        return create(size, size);
    }

    /**
     * Creates a new rectangle of the specified size with the specified origin.
     *
     * @param size The width and height of the rectangle.
     */
    public static Rectangle create(float size, RectangleOrigin origin) {
        return create(size, size, origin);
    }

    /**
     * Creates a new rectangle of the specified size with center at origin (0, 0).
     *
     * @param size The width and height of the rectangle.
     */
    public static Rectangle create(Point size) {
        return create(size.getX(), size.getY());
    }

    /**
     * Creates a new rectangle of the specified size with the specified origin.
     *
     * @param size   The width and height of the rectangle.
     * @param origin The origin of the rectangle.
     */
    public static Rectangle create(Point size, RectangleOrigin origin) {
        return create(size.getX(), size.getY(), origin);
    }

    /**
     * Creates a new rectangle of the specified size with center at origin (0, 0).
     *
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public static Rectangle create(float width, float height) {
        float halfWidth = 0.5f * width;
        float halfHeight = 0.5f * height;
        return new Rectangle(-halfWidth, -halfHeight, halfWidth, halfHeight);
    }

    /**
     * Creates a new rectangle of the specified size with the specified origin.
     *
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @param origin The origin of the rectangle.
     */
    public static Rectangle create(float width, float height, RectangleOrigin origin) {
        switch (origin) {
            case CENTER:
                return create(width, height);
            case LOWER_LEFT:
                return new Rectangle(0, 0, width, height);
            case LOWER_RIGHT:
                return new Rectangle(-width, 0, 0, height);
            case UPPER_LEFT:
                return new Rectangle(0, -height, width, 0);
            case UPPER_RIGHT:
                return new Rectangle(-width, -height, 0, 0);
            default:
                throw new IllegalStateException("Unexpected origin: " + origin);
        }
    }

    /**
     * Creates a new rectangle with the specified point added to its bounds.
     *
     * @param x The X coordinate of the added point.
     * @param y The Y coordinate of the added point.
     */
    public Rectangle add(float x, float y) {
        if (contains(x, y)) {
            return this;
        } else {
            return new Rectangle(
                    Math.min(this.lowerX, x),
                    Math.min(this.lowerY, y),
                    Math.max(this.upperX, x),
                    Math.max(this.upperY, y));
        }
    }

    /**
     * Creates a new rectangle with the specified point added to its bounds.
     */
    public Rectangle add(Point p) {
        return add(p.getX(), p.getY());
    }

    /**
     * Creates a new rectangle with the specified rectangle added to its bounds.
     *
     * @param lowerX The x-coordinate of the added lower left corner.
     * @param lowerY The y-coordinate of the added lower left corner.
     * @param upperX The x-coordinate of the added upper right corner.
     * @param upperY The y-coordinate of the added upper right corner.
     */
    public Rectangle add(float lowerX, float lowerY, float upperX, float upperY) {
        if (contains(lowerX, lowerY) && contains(upperX, upperY)) {
            return this;
        } else {
            return new Rectangle(
                    Math.min(this.lowerX, lowerX),
                    Math.min(this.lowerY, lowerY),
                    Math.max(this.upperX, upperX),
                    Math.max(this.upperY, upperY));
        }
    }

    /**
     * Creates a new rectangle with the specified rectangle added to its bounds.
     */
    public Rectangle add(Rectangle r) {
        return add(r.lowerX, r.lowerY, r.upperX, r.upperY);
    }

    /**
     * Checks if this rectangle contains the specified point.
     *
     * @param x The X coordinate of the point.
     * @param y The Y coordinate of the point.
     */
    public boolean contains(float x, float y) {
        return !(x < lowerX || y < lowerY || x > upperX || y > upperY);
    }

    /**
     * Checks if this rectangle contains the specified point.
     */
    public boolean contains(Point p) {
        return contains(p.getX(), p.getY());
    }

    /**
     * Checks if this rectangle contains the specified rectangle.
     *
     * @param r The rectangle.
     */
    public boolean contains(Rectangle r) {
        return contains(r.lowerX, r.lowerY, r.upperX, r.upperY);
    }

    /**
     * Checks if this rectangle contains the specified rectangle.
     *
     * @param lowerX The x-coordinate of the lower left corner.
     * @param lowerY The y-coordinate of the lower left corner.
     * @param upperX The x-coordinate of the upper right corner.
     * @param upperY The y-coordinate of the upper right corner.
     */
    public boolean contains(float lowerX, float lowerY, float upperX, float upperY) {
        return lowerX >= this.lowerX && upperX <= this.upperX
                && lowerY >= this.lowerY && upperY <= this.upperY;
    }

    /**
     * Checks if this rectangle intersects with the specified rectangle.
     */
    public boolean intersects(Rectangle r) {
        return intersects(r.lowerX, r.lowerY, r.upperX, r.upperY);
    }

    /**
     * Checks if this rectangle intersects with the specified rectangle.
     *
     * @param lowerX The x-coordinate of the lower left corner.
     * @param lowerY The y-coordinate of the lower left corner.
     * @param upperX The x-coordinate of the upper right corner.
     * @param upperY The y-coordinate of the upper right corner.
     */
    public boolean intersects(float lowerX, float lowerY, float upperX, float upperY) {
        return lowerX < this.upperX && upperX > this.lowerX
                && lowerY < this.upperY && upperY > this.lowerY;
    }

    /**
     * Creates a new rectangle representing the intersection of this rectangle and the specified rectangle.
     */
    public Rectangle getIntersection(Rectangle rect) {
        return getIntersection(rect.lowerX, rect.lowerY, rect.upperX, rect.upperY);
    }

    /**
     * Creates a new rectangle representing the intersection of this rectangle and the specified rectangle.
     *
     * @param lowerX The x-coordinate of the lower left corner.
     * @param lowerY The y-coordinate of the lower left corner.
     * @param upperX The x-coordinate of the upper right corner.
     * @param upperY The y-coordinate of the upper right corner.
     */
    public Rectangle getIntersection(float lowerX, float lowerY, float upperX, float upperY) {
        return new Rectangle(
                Math.max(this.lowerX, lowerX),
                Math.max(this.lowerY, lowerY),
                Math.min(this.upperX, upperX),
                Math.min(this.upperY, upperY)
        );
    }

    /**
     * Creates a new rectangle with the specified offset.
     */
    public Rectangle move(Point offset) {
        return move(offset.getX(), offset.getY());
    }

    /**
     * Creates a new rectangle with the specified offset.
     */
    public Rectangle move(float x, float y) {
        return new Rectangle(getLowerX() + x, getLowerY() + y, getUpperX() + x, getUpperY() + y);
    }

    /**
     * Creates a new rectangle with the specified padding.
     *
     * @param padding The padding added to each side.
     */
    public Rectangle pad(float padding) {
        return pad(padding, padding);
    }

    /**
     * Creates a new rectangle with the specified padding.
     *
     * @param horizontal The padding added to both horizontal sides.
     * @param vertical   The padding added to both vertical sides.
     */
    public Rectangle pad(float horizontal, float vertical) {
        return pad(horizontal, vertical, horizontal, vertical);
    }

    /**
     * Creates a new rectangle with the specified padding.
     *
     * @param left   The padding added to the left side.
     * @param bottom The padding added to the bottom side.
     * @param right  The padding added to the right side.
     * @param top    The padding added to the top side.
     */
    public Rectangle pad(float left, float bottom, float right, float top) {
        return new Rectangle(
                getLowerX() - left,
                getLowerY() - bottom,
                getUpperX() + right,
                getUpperY() + top);
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scale The scale of the new rectangle.
     */
    public Rectangle resize(float scale) {
        return resize(scale, scale);
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scale The scale of the new rectangle.
     */
    public Rectangle resize(Point scale) {
        return resize(scale.getX(), scale.getY());
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scale       The scale of the new rectangle.
     * @param centerPoint The scaling center point.
     */
    public Rectangle resize(Point scale, Point centerPoint) {
        return resize(scale.getX(), scale.getY(), centerPoint.getX(), centerPoint.getY());
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scaleX The horizontal scale of the new rectangle.
     * @param scaleY The vertical scale of the new rectangle.
     */
    public Rectangle resize(float scaleX, float scaleY) {
        return new Rectangle(lowerX * scaleX, lowerY * scaleY, upperX * scaleX, upperY * scaleY);
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scaleX       The horizontal scale of the new rectangle.
     * @param scaleY       The vertical scale of the new rectangle.
     * @param centerPointX The X coordinate of the scaling center point.
     * @param centerPointY The Y coordinate of the scaling center point.
     */
    public Rectangle resize(float scaleX, float scaleY, float centerPointX, float centerPointY) {
        float centerX = lowerX + getWidth() / 2;
        float centerY = lowerY + getHeight() / 2;

        float newCenterX = centerX + (centerPointX - centerX) / scaleX;
        float newCenterY = centerY + (centerPointY - centerY) / scaleY;

        return new Rectangle(
                newCenterX - (getWidth() / 2) * scaleX,
                newCenterY - (getHeight() / 2) * scaleY,
                newCenterX + (getWidth() / 2) * scaleX,
                newCenterY + (getHeight() / 2) * scaleY);
    }

    /**
     * Creates a new resized rectangle.
     *
     * @param scale The scale and relative position of the new rectangle.
     */
    public Rectangle resize(Rectangle scale) {
        float scaledWith = scale.getWidth() * getWidth();
        float scaledHeight = scale.getHeight() * getHeight();

        float offsetX = (scale.getLowerX() / scale.getWidth()) * scaledWith;
        float offsetY = (scale.getLowerY() / scale.getHeight()) * scaledHeight;

        return new Rectangle(
                offsetX,
                offsetY,
                scaledWith + offsetX,
                scaledHeight + offsetY);
    }

    /**
     * Creates a new resized rectangle with the specified maximum width and height.
     * The aspect ratio of the current rectangle will be respected.
     *
     * @param maxWidth  The maximum width of the new rectangle.
     * @param maxHeight The maximum height of the new rectangle.
     */
    public Rectangle restrict(float maxWidth, float maxHeight) {
        float width = getWidth();
        float height = getHeight();
        float aspectRatio = width / height;

        float restrictedWidth = Math.min(width, maxWidth);
        float restrictedHeight = Math.min(height, maxHeight);
        float restrictedAspectRatio = restrictedWidth / restrictedHeight;

        // Maintain aspect ratio:
        if (restrictedAspectRatio > aspectRatio) {
            restrictedWidth = restrictedHeight * aspectRatio;
        } else if (restrictedAspectRatio < aspectRatio) {
            restrictedHeight = restrictedWidth / aspectRatio;
        }

        float scaleX = restrictedWidth / width;
        float scaleY = restrictedHeight / height;

        return resize(scaleX, scaleY);
    }

    /**
     * Creates a new rectangle that represents the bounds of the corners,
     * when each corner has been rotated with the specified rotation.
     *
     * @param rotation The clockwise rotation angle, in degrees.
     * @param centerX  The X-coordinate of the rotation's center point.
     * @param centerY  The Y-coordinate of the rotation's center point.
     */
    public Rectangle rotate(float rotation, float centerX, float centerY) {
        float lowerX = Float.MAX_VALUE;
        float lowerY = Float.MAX_VALUE;
        float upperX = Float.MIN_VALUE;
        float upperY = Float.MIN_VALUE;

        // Rotate lower left corner
        Point rotationPoint = new Point(getLowerX(), getLowerY());
        rotationPoint.rotate(rotation, centerX, centerY);
        lowerX = Math.min(rotationPoint.getX(), lowerX);
        upperX = Math.max(rotationPoint.getX(), upperX);
        lowerY = Math.min(rotationPoint.getY(), lowerY);
        upperY = Math.max(rotationPoint.getY(), upperY);

        // Rotate upper left corner
        rotationPoint.set(getLowerX(), getUpperY());
        rotationPoint.rotate(rotation, centerX, centerY);
        lowerX = Math.min(rotationPoint.getX(), lowerX);
        upperX = Math.max(rotationPoint.getX(), upperX);
        lowerY = Math.min(rotationPoint.getY(), lowerY);
        upperY = Math.max(rotationPoint.getY(), upperY);

        // Rotate upper right corner
        rotationPoint.set(getUpperX(), getUpperY());
        rotationPoint.rotate(rotation, centerX, centerY);
        lowerX = Math.min(rotationPoint.getX(), lowerX);
        upperX = Math.max(rotationPoint.getX(), upperX);
        lowerY = Math.min(rotationPoint.getY(), lowerY);
        upperY = Math.max(rotationPoint.getY(), upperY);

        // Rotate lower right corner
        rotationPoint.set(getUpperX(), getLowerY());
        rotationPoint.rotate(rotation, centerX, centerY);
        lowerX = Math.min(rotationPoint.getX(), lowerX);
        upperX = Math.max(rotationPoint.getX(), upperX);
        lowerY = Math.min(rotationPoint.getY(), lowerY);
        upperY = Math.max(rotationPoint.getY(), upperY);

        return new Rectangle(lowerX, lowerY, upperX, upperY);
    }

    public Rectangle rotate(float rotation, Point center) {
        return rotate(rotation, center.getX(), center.getY());
    }

    public float getArea() {
        return getWidth() * getHeight();
    }

    public float getWidth() {
        return upperX - lowerX;
    }

    public float getHeight() {
        return upperY - lowerY;
    }

    public float getLowerX() {
        return lowerX;
    }

    public float getLowerY() {
        return lowerY;
    }

    public float getUpperX() {
        return upperX;
    }

    public float getUpperY() {
        return upperY;
    }

    public Point getCenter() {
        return new Point(getCenterX(), getCenterY());
    }

    public float getCenterX() {
        return 0.5f * lowerX + 0.5f * upperX;
    }

    public float getCenterY() {
        return 0.5f * lowerY + 0.5f * upperY;
    }

    public boolean isEmpty() {
        return upperY - lowerY == 0f
                && upperX - lowerX == 0f;
    }

    public boolean isInfinite() {
        return this.equals(Rectangle.INFINITE);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof Rectangle) {
            Rectangle other = (Rectangle) obj;
            return equals(other.lowerX, other.lowerY, other.upperX, other.upperY);
        }

        return false;
    }

    public boolean equals(float lowerX, float lowerY, float upperX, float upperY) {
        return lowerX == this.lowerX && lowerY == this.lowerY && upperX == this.upperX && upperY == this.upperY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerX, lowerY, upperX, upperY);
    }
}
