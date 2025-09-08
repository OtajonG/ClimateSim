#version 330 core
out vec4 FragColor;

in vec2 vTexCoord;
in vec3 vNormal;
in vec3 vFragPos;

uniform sampler2D dayTexture;
uniform sampler2D nightTexture;
uniform sampler2D cloudsTexture;
uniform sampler2D specularTexture;

uniform vec3 lightPos; // Position of the sun
uniform vec3 viewPos;  // Position of the camera

void main()
{
    // Texture colors
    vec3 dayColor = texture(dayTexture, vTexCoord).rgb;
    vec3 nightColor = texture(nightTexture, vTexCoord).rgb;
    vec3 cloudsColor = texture(cloudsTexture, vTexCoord).rgb;
    float specularMask = texture(specularTexture, vTexCoord).r; // Use red channel for ocean mask

    // Lighting calculations
    vec3 norm = normalize(vNormal);
    vec3 lightDir = normalize(lightPos - vFragPos);

    // Lambertian diffuse lighting (how much the surface faces the sun)
    float diff = max(dot(norm, lightDir), 0.0);

    // Determine day/night transition
    float dayNightMix = smoothstep(-0.1, 0.2, diff);

    // Mix day and night textures
    vec3 surfaceColor = mix(nightColor, dayColor, dayNightMix);

    // Add clouds on top
    vec3 finalColor = mix(surfaceColor, cloudsColor, cloudsColor.r * 0.8 * dayNightMix); // Clouds only visible on day side

    // Specular lighting (ocean reflection)
    vec3 viewDir = normalize(viewPos - vFragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    vec3 specular = vec3(0.8) * spec * specularMask; // Use mask to only show on water

    // Add specular reflection and a touch of ambient light
    finalColor = finalColor * (dayNightMix * 0.8 + 0.2) + specular;

    FragColor = vec4(finalColor, 1.0);
}