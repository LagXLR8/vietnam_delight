#version 150

uniform sampler2D Sampler0; // blurred bloom color
uniform sampler2D Sampler1; // bloom SOURCE depth (recorded when the glow source drew into bloomTarget)
uniform sampler2D Sampler2; // CURRENT main scene depth (fresh, includes entities/translucent drawn since)

uniform vec2 ScreenSize;
uniform float Strength;

// Projection matrix terms needed to turn the non-linear depth-buffer value
// back into a real view-space distance (in blocks). See BloomRenderer#
// compositeChannel -- these come straight from RenderSystem's projection
// matrix each frame (element [2][2] and [3][2]).
uniform float ProjA;
uniform float ProjB;

out vec4 fragColor;

// Raw depth-buffer values are NOT linear with distance -- resolution is
// packed near the camera and gets heavily compressed far away. Comparing
// raw depth with a fixed epsilon (the old approach) works fine up close
// (a full block of real distance = a huge raw-depth delta) but silently
// stops working at range (a full block of real distance can collapse to
// far less than the epsilon), which is why glow used to "shine through"
// solid blocks only once you backed away from them. Converting both
// samples to a real view-space distance first makes the epsilon mean the
// same physical thing (blocks) at any range.
float linearDepth(float d) {
    float ndcZ = d * 2.0 - 1.0;
    return ProjB / (ndcZ + ProjA);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;

    float sourceDepth = linearDepth(texture(Sampler1, uv).r);
    float currentDepth = linearDepth(texture(Sampler2, uv).r);

    const float WORLD_EPSILON = 0.15; // blocks -- constant regardless of distance now
    if (currentDepth + WORLD_EPSILON < sourceDepth) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }

    vec4 c = texture(Sampler0, uv);
    fragColor = vec4(c.rgb * Strength, 1.0);
}
