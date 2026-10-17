#version 150

uniform sampler2D Sampler0; // color to blur
uniform sampler2D Sampler1; // reference depth (the channel's ORIGINAL, unblurred depth -- same for both H and V passes)

uniform vec2 ScreenSize;
uniform vec2 BlurDir;
uniform float Radius;

// Same projection terms as bloom_composite.fsh -- needed to turn the
// non-linear depth-buffer value into a real view-space distance (blocks)
// so the edge tolerance below means the same physical thing at any range.
uniform float ProjA;
uniform float ProjB;

out vec4 fragColor;

float linearDepth(float d) {
    float ndcZ = d * 2.0 - 1.0;
    return ProjB / (ndcZ + ProjA);
}

void main() {
    vec2 uv = gl_FragCoord.xy / ScreenSize;
    vec2 texel = BlurDir / ScreenSize;

    float centerDepth = linearDepth(texture(Sampler1, uv).r);

    vec4 sum = vec4(0.0);
    float totalWeight = 0.0;

    float steps = max(Radius, 1.0);
    // How much real depth difference (in blocks) still counts as "close
    // enough to belong to the same glow surface" for blur purposes. A
    // single flower/petal quad can already span close to a block of depth
    // on its own when viewed at an angle, so this needs to be loose enough
    // to not chop up the glow's own soft edge -- while still being far
    // smaller than the gap to an unrelated background surface (which is
    // what caused bloom to bleed through solid blocks before). Falls off
    // smoothly instead of a hard cutoff so the halo still looks soft.
    const float EDGE_TOLERANCE = 2.0;

    for (float i = -steps; i <= steps; i += 1.0) {
        vec2 sampleUv = uv + texel * i;
        float sampleDepth = linearDepth(texture(Sampler1, sampleUv).r);
        float depthDiff = abs(sampleDepth - centerDepth);
        float depthWeight = clamp(1.0 - depthDiff / EDGE_TOLERANCE, 0.0, 1.0);
        if (depthWeight <= 0.0) {
            continue; // clearly a separate surface -- don't bleed onto/from it
        }
        float weight = (steps + 1.0 - abs(i)) * depthWeight;
        sum += texture(Sampler0, sampleUv) * weight;
        totalWeight += weight;
    }

    fragColor = totalWeight > 0.0 ? sum / totalWeight : vec4(0.0);
}
