package net.survival.client.graphics2.internal.d3d11;

import dev.josh127.libwin32.D3D11;
import net.survival.client.graphics2.internal.FragmentShader;

/**
 * Represents an executable program used to output fragments to ColorTargets.
 */
class D3D11FragmentShader implements FragmentShader
{
    final D3D11.PixelShader shader;

    /**
     * Constructs a D3D11FragmentShader.
     * 
     * @param shader the underlying shader
     */
    D3D11FragmentShader(D3D11.PixelShader shader) {
        this.shader = shader;
    }
}