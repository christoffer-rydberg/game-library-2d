package com.gamelibrary2d.glUtil;

import com.gamelibrary2d.common.io.BufferUtils;

import java.nio.FloatBuffer;

class MatrixBuffer {

    private final float[] id = createIdentityMatrix();
    private final float[] current = new float[16];
    private final float[] result = new float[16];
    private final FloatBuffer buffer = BufferUtils.createFloatBuffer(4 * 16);

    MatrixBuffer() {
        clear();
    }

    private static float[] createIdentityMatrix() {
        float[] identity = new float[16];
        identity[0] = 1;
        identity[5] = 1;
        identity[10] = 1;
        identity[15] = 1;
        return identity;
    }

    /**
     * Creates a orthographic projection matrix. Similar to
     * <code>glOrtho(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane
     * @param far    Coordinate for the far depth clipping pane
     * @return Orthographic matrix
     */
    static MatrixBuffer orthographic(float left, float right, float bottom, float top, float near, float far) {
        MatrixBuffer ortho = new MatrixBuffer();
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);
        ortho.current[0] = 2f / (right - left);
        ortho.current[5] = 2f / (top - bottom);
        ortho.current[10] = -2f / (far - near);
        ortho.current[12] = tx;
        ortho.current[13] = ty;
        ortho.current[14] = tz;
        return ortho;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>glFrustum(left, right, bottom, top, near, far)</code>.
     *
     * @param left   Coordinate for the left vertical clipping pane
     * @param right  Coordinate for the right vertical clipping pane
     * @param bottom Coordinate for the bottom horizontal clipping pane
     * @param top    Coordinate for the bottom horizontal clipping pane
     * @param near   Coordinate for the near depth clipping pane, must be positive
     * @param far    Coordinate for the far depth clipping pane, must be positive
     * @return Perspective matrix
     */
    public static MatrixBuffer frustum(float left, float right, float bottom, float top, float near, float far) {

        MatrixBuffer frustum = new MatrixBuffer();

        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        frustum.current[0] = (2f * near) / (right - left);
        frustum.current[5] = (2f * near) / (top - bottom);
        frustum.current[8] = a;
        frustum.current[9] = b;
        frustum.current[10] = c;
        frustum.current[11] = -1f;
        frustum.current[14] = d;
        frustum.current[15] = 0f;

        return frustum;
    }

    /**
     * Creates a perspective projection matrix. Similar to
     * <code>gluPerspective(fovy, aspec, zNear, zFar)</code>.
     *
     * @param fovy   Field of view angle in degrees
     * @param aspect The aspect ratio is the ratio of width to height
     * @param near   Distance from the viewer to the near clipping plane, must be
     *               positive
     * @param far    Distance from the viewer to the far clipping plane, must be
     *               positive
     * @return Perspective matrix
     */
    public static MatrixBuffer perspective(float fovy, float aspect, float near, float far) {
        MatrixBuffer perspective = new MatrixBuffer();
        float f = (float) (1f / Math.tan(Math.toRadians(fovy) / 2f));
        perspective.current[0] = f / aspect;
        perspective.current[5] = f;
        perspective.current[10] = (far + near) / (near - far);
        perspective.current[11] = -1f;
        perspective.current[14] = (2f * far * near) / (near - far);
        perspective.current[15] = 0f;
        return perspective;
    }

    public void clear() {
        deserialize(id);
    }

    void serialize(float[] array) {
        copyValues(current, array);
    }

    void deserialize(float[] array) {
        copyValues(array, current);
    }

    FloatBuffer getFloatBuffer() {
        buffer.position(0);
        buffer.put(current);
        buffer.flip();
        return buffer;
    }

    void translate(float x, float y, float z) {
        current[12] = current[0] * x + current[4] * y + current[8] * z + current[12];
        current[13] = current[1] * x + current[5] * y + current[9] * z + current[13];
        current[14] = current[2] * x + current[6] * y + current[10] * z + current[14];
        current[15] = current[3] * x + current[7] * y + current[11] * z + current[15];
    }

    public void scale(float x, float y, float z) {
        current[0] *= x;
        current[1] *= x;
        current[2] *= x;
        current[3] *= x;
        current[4] *= y;
        current[5] *= y;
        current[6] *= y;
        current[7] *= y;
        current[8] *= z;
        current[9] *= z;
        current[10] *= z;
        current[11] *= z;
    }

    void rotateZ(float angle) {

        double angleRadians = Math.toRadians(angle);
        float c = (float) Math.cos(angleRadians);
        float s = (float) Math.sin(angleRadians);

        float other00 = c;
        float other01 = s;
        float other10 = -s;
        float other11 = c;

        result[0] = current[0] * other00 + current[4] * other01;
        result[1] = current[1] * other00 + current[5] * other01;
        result[2] = current[2] * other00 + current[6] * other01;
        result[3] = current[3] * other00 + current[7] * other01;

        result[4] = current[0] * other10 + current[4] * other11;
        result[5] = current[1] * other10 + current[5] * other11;
        result[6] = current[2] * other10 + current[6] * other11;
        result[7] = current[3] * other10 + current[7] * other11;

        System.arraycopy(result, 0, current, 0, 8);
    }

    void rotate(float angle, float x, float y, float z) {

        double angleRadians = Math.toRadians(angle);
        float c = (float) Math.cos(angleRadians);
        float s = (float) Math.sin(angleRadians);

        float other00 = x * x * (1f - c) + c;
        float other01 = y * x * (1f - c) + z * s;
        float other02 = x * z * (1f - c) - y * s;
        float other10 = x * y * (1f - c) - z * s;
        float other11 = y * y * (1f - c) + c;
        float other12 = y * z * (1f - c) + x * s;
        float other20 = x * z * (1f - c) + y * s;
        float other21 = y * z * (1f - c) - x * s;
        float other22 = z * z * (1f - c) + c;

        result[0] = current[0] * other00 + current[4] * other01 + current[8] * other02;
        result[1] = current[1] * other00 + current[5] * other01 + current[9] * other02;
        result[2] = current[2] * other00 + current[6] * other01 + current[10] * other02;
        result[3] = current[3] * other00 + current[7] * other01 + current[11] * other02;

        result[4] = current[0] * other10 + current[4] * other11 + current[8] * other12;
        result[5] = current[1] * other10 + current[5] * other11 + current[9] * other12;
        result[6] = current[2] * other10 + current[6] * other11 + current[10] * other12;
        result[7] = current[3] * other10 + current[7] * other11 + current[11] * other12;

        result[8] = current[0] * other20 + current[4] * other21 + current[8] * other22;
        result[9] = current[1] * other20 + current[5] * other21 + current[9] * other22;
        result[10] = current[2] * other20 + current[6] * other21 + current[10] * other22;
        result[11] = current[3] * other20 + current[7] * other21 + current[11] * other22;

        System.arraycopy(result, 0, current, 0, 12);
    }

    void multiply(MatrixBuffer other) {
        multiply(other.current);
    }

    private void multiply(float[] other) {
        result[0] = current[0] * other[0] + current[4] * other[1] + current[8] * other[2] + current[12] * other[3];
        result[1] = current[1] * other[0] + current[5] * other[1] + current[9] * other[2] + current[13] * other[3];
        result[2] = current[2] * other[0] + current[6] * other[1] + current[10] * other[2] + current[14] * other[3];
        result[3] = current[3] * other[0] + current[7] * other[1] + current[11] * other[2] + current[15] * other[3];

        result[4] = current[0] * other[4] + current[4] * other[5] + current[8] * other[6] + current[12] * other[7];
        result[5] = current[1] * other[4] + current[5] * other[5] + current[9] * other[6] + current[13] * other[7];
        result[6] = current[2] * other[4] + current[6] * other[5] + current[10] * other[6] + current[14] * other[7];
        result[7] = current[3] * other[4] + current[7] * other[5] + current[11] * other[6] + current[15] * other[7];

        result[8] = current[0] * other[8] + current[4] * other[9] + current[8] * other[10] + current[12] * other[11];
        result[9] = current[1] * other[8] + current[5] * other[9] + current[9] * other[10] + current[13] * other[11];
        result[10] = current[2] * other[8] + current[6] * other[9] + current[10] * other[10] + current[14] * other[11];
        result[11] = current[3] * other[8] + current[7] * other[9] + current[11] * other[10] + current[15] * other[11];

        result[12] = current[0] * other[12] + current[4] * other[13] + current[8] * other[14] + current[12] * other[15];
        result[13] = current[1] * other[12] + current[5] * other[13] + current[9] * other[14] + current[13] * other[15];
        result[14] = current[2] * other[12] + current[6] * other[13] + current[10] * other[14]
                + current[14] * other[15];
        result[15] = current[3] * other[12] + current[7] * other[13] + current[11] * other[14]
                + current[15] * other[15];

        copyValues(result, current);
    }

    private void copyValues(float[] from, float[] to) {
        System.arraycopy(from, 0, to, 0, 16);
    }
}