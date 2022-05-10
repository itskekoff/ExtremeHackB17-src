package paulscode.sound;

import paulscode.sound.Vector3D;

public class ListenerData {
    public Vector3D position;
    public Vector3D lookAt;
    public Vector3D up;
    public Vector3D velocity;
    public float angle = 0.0f;

    public ListenerData() {
        this.position = new Vector3D(0.0f, 0.0f, 0.0f);
        this.lookAt = new Vector3D(0.0f, 0.0f, -1.0f);
        this.up = new Vector3D(0.0f, 1.0f, 0.0f);
        this.velocity = new Vector3D(0.0f, 0.0f, 0.0f);
        this.angle = 0.0f;
    }

    public ListenerData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a2) {
        this.position = new Vector3D(pX, pY, pZ);
        this.lookAt = new Vector3D(lX, lY, lZ);
        this.up = new Vector3D(uX, uY, uZ);
        this.velocity = new Vector3D(0.0f, 0.0f, 0.0f);
        this.angle = a2;
    }

    public ListenerData(Vector3D p2, Vector3D l2, Vector3D u2, float a2) {
        this.position = p2.clone();
        this.lookAt = l2.clone();
        this.up = u2.clone();
        this.velocity = new Vector3D(0.0f, 0.0f, 0.0f);
        this.angle = a2;
    }

    public void setData(float pX, float pY, float pZ, float lX, float lY, float lZ, float uX, float uY, float uZ, float a2) {
        this.position.x = pX;
        this.position.y = pY;
        this.position.z = pZ;
        this.lookAt.x = lX;
        this.lookAt.y = lY;
        this.lookAt.z = lZ;
        this.up.x = uX;
        this.up.y = uY;
        this.up.z = uZ;
        this.angle = a2;
    }

    public void setData(Vector3D p2, Vector3D l2, Vector3D u2, float a2) {
        this.position.x = p2.x;
        this.position.y = p2.y;
        this.position.z = p2.z;
        this.lookAt.x = l2.x;
        this.lookAt.y = l2.y;
        this.lookAt.z = l2.z;
        this.up.x = u2.x;
        this.up.y = u2.y;
        this.up.z = u2.z;
        this.angle = a2;
    }

    public void setData(ListenerData l2) {
        this.position.x = l2.position.x;
        this.position.y = l2.position.y;
        this.position.z = l2.position.z;
        this.lookAt.x = l2.lookAt.x;
        this.lookAt.y = l2.lookAt.y;
        this.lookAt.z = l2.lookAt.z;
        this.up.x = l2.up.x;
        this.up.y = l2.up.y;
        this.up.z = l2.up.z;
        this.angle = l2.angle;
    }

    public void setPosition(float x2, float y2, float z2) {
        this.position.x = x2;
        this.position.y = y2;
        this.position.z = z2;
    }

    public void setPosition(Vector3D p2) {
        this.position.x = p2.x;
        this.position.y = p2.y;
        this.position.z = p2.z;
    }

    public void setOrientation(float lX, float lY, float lZ, float uX, float uY, float uZ) {
        this.lookAt.x = lX;
        this.lookAt.y = lY;
        this.lookAt.z = lZ;
        this.up.x = uX;
        this.up.y = uY;
        this.up.z = uZ;
    }

    public void setOrientation(Vector3D l2, Vector3D u2) {
        this.lookAt.x = l2.x;
        this.lookAt.y = l2.y;
        this.lookAt.z = l2.z;
        this.up.x = u2.x;
        this.up.y = u2.y;
        this.up.z = u2.z;
    }

    public void setVelocity(Vector3D v2) {
        this.velocity.x = v2.x;
        this.velocity.y = v2.y;
        this.velocity.z = v2.z;
    }

    public void setVelocity(float x2, float y2, float z2) {
        this.velocity.x = x2;
        this.velocity.y = y2;
        this.velocity.z = z2;
    }

    public void setAngle(float a2) {
        this.angle = a2;
        this.lookAt.x = -1.0f * (float)Math.sin(this.angle);
        this.lookAt.z = -1.0f * (float)Math.cos(this.angle);
    }
}

