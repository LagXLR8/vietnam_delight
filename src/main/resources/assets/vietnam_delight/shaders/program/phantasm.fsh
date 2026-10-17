#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    // Center sample
    vec4 center = texture(DiffuseSampler, texCoord);
    vec3 c11 = center.rgb;

    // 3x3 Neighborhood samples for edge detection
    vec3 c00 = texture(DiffuseSampler, texCoord + vec2(-oneTexel.x, -oneTexel.y)).rgb;
    vec3 c10 = texture(DiffuseSampler, texCoord + vec2(        0.0, -oneTexel.y)).rgb;
    vec3 c20 = texture(DiffuseSampler, texCoord + vec2( oneTexel.x, -oneTexel.y)).rgb;

    vec3 c01 = texture(DiffuseSampler, texCoord + vec2(-oneTexel.x,         0.0)).rgb;
    vec3 c21 = texture(DiffuseSampler, texCoord + vec2( oneTexel.x,         0.0)).rgb;

    vec3 c02 = texture(DiffuseSampler, texCoord + vec2(-oneTexel.x,  oneTexel.y)).rgb;
    vec3 c12 = texture(DiffuseSampler, texCoord + vec2(        0.0,  oneTexel.y)).rgb;
    vec3 c22 = texture(DiffuseSampler, texCoord + vec2( oneTexel.x,  oneTexel.y)).rgb;

    // Sobel gradient vectors (horizontal and vertical color differences)
    vec3 gx = -1.0 * c00 + 1.0 * c20
              - 2.0 * c01 + 2.0 * c21
              - 1.0 * c02 + 1.0 * c22;

    vec3 gy = -1.0 * c00 - 2.0 * c10 - 1.0 * c20
              + 1.0 * c02 + 2.0 * c12 + 1.0 * c22;

    float edgeX = length(gx);
    float edgeY = length(gy);
    float edgeMag = edgeX + edgeY;

    // Balanced edge detection (filters out fine texture noise for clear geometric edges)
    float edge = smoothstep(0.18, 0.48, edgeMag);

    // Wide 4-tap sample for soft luminescent glow / bloom aura around block borders
    vec3 g0 = texture(DiffuseSampler, texCoord + vec2(-2.0 * oneTexel.x, 0.0)).rgb;
    vec3 g1 = texture(DiffuseSampler, texCoord + vec2( 2.0 * oneTexel.x, 0.0)).rgb;
    vec3 g2 = texture(DiffuseSampler, texCoord + vec2(0.0, -2.0 * oneTexel.y)).rgb;
    vec3 g3 = texture(DiffuseSampler, texCoord + vec2(0.0,  2.0 * oneTexel.y)).rgb;

    float wideDelta = (length(g0 - c11) + length(g1 - c11) + length(g2 - c11) + length(g3 - c11)) * 0.25;
    float glow = smoothstep(0.12, 0.45, wideDelta);

    // Elegant, soothing neon colors (toned down to prevent glare/eye strain)
    // Deep Violet-Purple: rgb(0.62, 0.16, 0.88)
    // Soft Electric Cyan: rgb(0.08, 0.75, 0.85)
    vec3 neonPurple = vec3(0.62, 0.16, 0.88);
    vec3 neonCyan   = vec3(0.08, 0.75, 0.85);

    // Interlace neon purple and cyan based on edge orientation and screen position
    float colorMix = clamp((edgeX - edgeY) * 1.8 + 0.5, 0.0, 1.0);
    colorMix = mix(colorMix, sin(texCoord.x * 20.0 + texCoord.y * 20.0) * 0.5 + 0.5, 0.25);
    vec3 neonEdgeColor = mix(neonPurple, neonCyan, colorMix);

    // Controlled border radiance (crisp outline without blinding glow)
    vec3 edgeRadiance = neonEdgeColor * (edge * 0.90 + glow * 0.20);

    // Deeply darken the surrounding environment
    float luma = dot(c11, vec3(0.299, 0.587, 0.114));
    vec3 darkEnvironment = mix(vec3(luma * 0.04), c11 * 0.08, 0.5);

    // Subtle dark purple ambient tone in the deep shadows
    darkEnvironment += vec3(0.010, 0.003, 0.018);

    // Radial vignette for deep darkness at screen margins
    vec2 uvDist = texCoord - 0.5;
    float vignette = clamp(1.0 - dot(uvDist, uvDist) * 1.6, 0.0, 1.0);
    darkEnvironment *= vignette;

    // Final composite: deeply darkened background with pleasant neon outlines
    vec3 finalColor = min(darkEnvironment + edgeRadiance, vec3(1.0));

    fragColor = vec4(finalColor, center.a);
}
