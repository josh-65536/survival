package net.survival.graphics;

import java.util.ArrayList;

import org.joml.Matrix4f;

import net.survival.graphics.model.ModelRenderer;
import net.survival.graphics.model.StaticModel;
import net.survival.graphics.opengl.GLMatrixStack;
import net.survival.render.message.DrawModelMessage;

class ActorDisplay
{
    private final ArrayList<DrawModelMessage> modelsToDraw = new ArrayList<>();
    private final Camera camera;

    private Matrix4f cameraViewMatrix = new Matrix4f();
    private Matrix4f cameraProjectionMatrix = new Matrix4f();

    public ActorDisplay(Camera camera) {
        this.camera = camera;
    }

    public void drawModel(DrawModelMessage model) {
        modelsToDraw.add(model);
    }

    public void display() {
        cameraViewMatrix.identity();
        camera.getViewMatrix(cameraViewMatrix);
        cameraProjectionMatrix.identity();
        camera.getProjectionMatrix(cameraProjectionMatrix);

        GLMatrixStack.setProjectionMatrix(cameraProjectionMatrix);
        GLMatrixStack.push();
        GLMatrixStack.loadIdentity();

        displayActors(cameraViewMatrix);

        GLMatrixStack.pop();

        modelsToDraw.clear();
    }

    private void displayActors(Matrix4f viewMatrix) {
        GLMatrixStack.push();
        GLMatrixStack.load(viewMatrix);

        for (var actor : modelsToDraw)
            displayModel(actor);

        GLMatrixStack.pop();
    }

    private void displayModel(DrawModelMessage model) {
        if (model.getModelType() != null) {
            GLMatrixStack.push();
            GLMatrixStack.translate(
                    (float) model.getX(),
                    (float) model.getY(),
                    (float) model.getZ());
            GLMatrixStack.rotate((float) model.getYaw(), 0.0f, 1.0f, 0.0f);
            GLMatrixStack.rotate((float) model.getPitch(), 1.0f, 0.0f, 0.0f);
            GLMatrixStack.rotate((float) model.getRoll(), 0.0f, 0.0f, 1.0f);
    
            var displayable = StaticModel.fromModelType(model.getModelType());
            ModelRenderer.displayStaticModel(displayable);
    
            GLMatrixStack.pop();
        }
    }
}