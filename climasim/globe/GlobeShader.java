package com.climasim.globe;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * Enhanced shader for realistic Earth rendering with satellite textures
 */
public class GlobeShader {

    private int shaderProgram;
    private int vertexShader;
    private int fragmentShader;

    private static final String VERTEX_SHADER_SOURCE = "#version 330 core\n" +
            "layout (location = 0) in vec3 aPos;\n" +
            "layout (location = 1) in vec3 aNormal;\n" +
            "layout (location = 2) in vec2 aTexCoord;\n" +
            "layout (location = 3) in vec3 aTangent;\n" +
            "layout (location = 4) in vec3 aBitangent;\n" +
            "layout (location = 5) in float aElevation;\n" +
            "layout (location = 6) in float aMoisture;\n" +
            "\n" +
            "out vec3 FragPos;\n" +
            "out vec3 Normal;\n" +
            "out vec2 TexCoord;\n" +
            "out vec3 WorldPos;\n" +
            "out vec3 Tangent;\n" +
            "out vec3 Bitangent;\n" +
            "out float Elevation;\n" +
            "out float Moisture;\n" +
            "out vec3 ViewDir;\n" +
            "out mat3 TBN;\n" +
            "\n" +
            "uniform mat4 model;\n" +
            "uniform mat4 view;\n" +
            "uniform mat4 projection;\n" +
            "uniform vec3 viewPos;\n" +
            "\n" +
            "void main() {\n" +
            "    FragPos = vec3(model * vec4(aPos, 1.0));\n" +
            "    Normal = normalize(mat3(transpose(inverse(model))) * aNormal);\n" +
            "    TexCoord = aTexCoord;\n" +
            "    WorldPos = FragPos;\n" +
            "    Tangent = normalize(mat3(model) * aTangent);\n" +
            "    Bitangent = normalize(mat3(model) * aBitangent);\n" +
            "    Elevation = aElevation;\n" +
            "    Moisture = aMoisture;\n" +
            "    ViewDir = normalize(viewPos - FragPos);\n" +
            "    \n" +
            "    // Create TBN matrix for normal mapping\n" +
            "    vec3 T = normalize(Tangent);\n" +
            "    vec3 B = normalize(Bitangent);\n" +
            "    vec3 N = normalize(Normal);\n" +
            "    TBN = mat3(T, B, N);\n" +
            "    \n" +
            "    gl_Position = projection * view * vec4(FragPos, 1.0);\n" +
            "}";

    private static final String FRAGMENT_SHADER_SOURCE = "#version 330 core\n" +
            "out vec4 FragColor;\n" +
            "\n" +
            "in vec3 FragPos;\n" +
            "in vec3 Normal;\n" +
            "in vec2 TexCoord;\n" +
            "in vec3 WorldPos;\n" +
            "in vec3 Tangent;\n" +
            "in vec3 Bitangent;\n" +
            "in float Elevation;\n" +
            "in float Moisture;\n" +
            "in vec3 ViewDir;\n" +
            "in mat3 TBN;\n" +
            "\n" +
            "// Real Earth textures\n" +
            "uniform sampler2D dayTexture;          // Blue Marble day satellite imagery\n" +
            "uniform sampler2D nightTexture;        // Night lights\n" +
            "uniform sampler2D cloudsTexture;       // Cloud cover\n" +
            "uniform sampler2D normalTexture;       // Elevation normal map\n" +
            "uniform sampler2D specularTexture;     // Water/land specular map\n" +
            "uniform sampler2D bathymetryTexture;   // Ocean depth\n" +
            "uniform sampler2D vegetationTexture;   // Vegetation density\n" +
            "\n" +
            "// Lighting uniforms\n" +
            "uniform vec3 sunPosition;\n" +
            "uniform float sunIntensity;\n" +
            "uniform vec3 ambientColor;\n" +
            "uniform vec3 sunColor;\n" +
            "uniform float atmosphereRadius;\n" +
            "uniform vec3 scatteringCoeff;\n" +
            "uniform float atmosphereDensity;\n" +
            "uniform float ozoneIntensity;\n" +
            "uniform float waveStrength;\n" +
            "uniform float waveSpeed;\n" +
            "uniform float oceanSpecular;\n" +
            "uniform vec3 oceanColor;\n" +
            "uniform float oceanFresnel;\n" +
            "uniform float time;\n" +
            "uniform float cloudHeight;\n" +
            "uniform float cloudDensity;\n" +
            "uniform float cloudShadowing;\n" +
            "uniform vec2 cloudOffset;\n" +
            "uniform float iceCapsIntensity;\n" +
            "uniform vec3 iceColor;\n" +
            "uniform float polarBrightness;\n" +
            "uniform float cityLightIntensity;\n" +
            "uniform vec3 cityLightColor;\n" +
            "uniform float nightSideVisibility;\n" +
            "uniform float climateIntensity;\n" +
            "uniform float vegetationDensity;\n" +
            "uniform float desertificationLevel;\n" +
            "uniform float atmospherePass;\n" +
            "\n" +
            "// Texture blending parameters\n" +
            "uniform float textureBlend;\n" +
            "uniform float seasonalVariation;\n" +
            "uniform float climateDataInfluence;\n" +
            "\n" +
            "// Climate change parameters\n" +
            "uniform float temperatureChange;\n" +
            "uniform float iceCapReduction;\n" +
            "uniform float forestLoss;\n" +
            "uniform float oceanAcidification;\n" +
            "uniform float pollutionLevel;\n" +
            "\n" +
            "// Enhanced normal mapping\n" +
            "vec3 getNormalFromMap(vec2 uv) {\n" +
            "    vec3 normalMap = texture(normalTexture, uv).rgb * 2.0 - 1.0;\n" +
            "    return normalize(TBN * normalMap);\n" +
            "}\n" +
            "\n" +
            "// Advanced cloud rendering with animation\n" +
            "float getAdvancedClouds(vec2 uv) {\n" +
            "    vec2 animatedUV = uv + cloudOffset;\n" +
            "    \n" +
            "    // Sample cloud texture with multiple octaves\n" +
            "    float clouds1 = texture(cloudsTexture, animatedUV).r;\n" +
            "    float clouds2 = texture(cloudsTexture, animatedUV * 2.0 + vec2(0.1, 0.2)).r;\n" +
            "    float clouds3 = texture(cloudsTexture, animatedUV * 4.0 + vec2(0.3, 0.1)).r;\n" +
            "    \n" +
            "    float finalClouds = clouds1 * 0.6 + clouds2 * 0.3 + clouds3 * 0.1;\n" +
            "    \n" +
            "    // Add climate change effects (more chaotic weather)\n" +
            "    finalClouds += temperatureChange * 0.1 * sin(uv.x * 50.0 + time) * cos(uv.y * 40.0 + time);\n" +
            "    \n" +
            "    return clamp(finalClouds * cloudDensity, 0.0, 1.0);\n" +
            "}\n" +
            "\n" +
            "// Realistic city lights from texture\n" +
            "vec3 getCityLights(vec2 uv, float dotLightSun) {\n" +
            "    if (dotLightSun > 0.0) return vec3(0.0);\n" +
            "    \n" +
            "    vec3 nightLights = texture(nightTexture, uv).rgb;\n" +
            "    \n" +
            "    // Apply pollution effects (dimmer, more orange lights)\n" +
            "    vec3 pollutedLights = mix(nightLights, nightLights * vec3(1.0, 0.7, 0.4), pollutionLevel * 0.6);\n" +
            "    \n" +
            "    return pollutedLights * cityLightColor * cityLightIntensity * (1.0 - dotLightSun);\n" +
            "}\n" +
            "\n" +
            "// Advanced ocean rendering\n" +
            "vec3 getOceanColor(vec2 uv, vec3 normal, vec3 sunDir, vec3 viewDir, float dotLightSun) {\n" +
            "    // Sample bathymetry for depth-based coloring\n" +
            "    float depth = texture(bathymetryTexture, uv).r;\n" +
            "    \n" +
            "    // Base ocean colors based on depth\n" +
            "    vec3 deepWater = vec3(0.0, 0.1, 0.3);\n" +
            "    vec3 shallowWater = vec3(0.2, 0.6, 0.8);\n" +
            "    vec3 baseOceanColor = mix(deepWater, shallowWater, depth);\n" +
            "    \n" +
            "    // Apply ocean acidification (more greenish, less blue)\n" +
            "    baseOceanColor = mix(baseOceanColor, vec3(0.3, 0.4, 0.3), oceanAcidification * 0.5);\n" +
            "    \n" +
            "    // Animated waves\n" +
            "    vec2 waveUV = uv + vec2(sin(time * waveSpeed + uv.x * 30.0), \n" +
            "                             cos(time * waveSpeed * 0.8 + uv.y * 25.0)) * waveStrength;\n" +
            "    \n" +
            "    // Wave normal perturbation\n" +
            "    vec3 waveNormal = normal + vec3(sin(waveUV.x * 100.0), 0.0, cos(waveUV.y * 80.0)) * 0.1;\n" +
            "    waveNormal = normalize(waveNormal);\n" +
            "    \n" +
            "    // Specular reflection\n" +
            "    vec3 reflectDir = reflect(-sunDir, waveNormal);\n" +
            "    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64.0);\n" +
            "    \n" +
            "    // Fresnel effect\n" +
            "    float fresnel = pow(1.0 - max(dot(waveNormal, viewDir), 0.0), 3.0);\n" +
            "    \n" +
            "    vec3 specular = sunColor * spec * oceanSpecular * max(dotLightSun, 0.0);\n" +
            "    \n" +
            "    return mix(baseOceanColor, baseOceanColor + specular, fresnel);\n" +
            "}\n" +
            "\n" +
            "// Enhanced land rendering with climate effects\n" +
            "vec3 getLandColor(vec2 uv) {\n" +
            "    // Sample day texture (satellite imagery)\n" +
            "    vec3 dayColor = texture(dayTexture, uv).rgb;\n" +
            "    \n" +
            "    // Sample vegetation data\n" +
            "    float vegDensity = texture(vegetationTexture, uv).r;\n" +
            "    \n" +
            "    // Apply deforestation effects\n" +
            "    vegDensity *= (1.0 - forestLoss);\n" +
            "    \n" +
            "    // Apply desertification (make areas more brown/yellow)\n" +
            "    vec3 desertColor = vec3(0.8, 0.7, 0.5);\n" +
            "    dayColor = mix(dayColor, desertColor, desertificationLevel * (1.0 - vegDensity));\n" +
            "    \n" +
            "    // Apply temperature change effects (less green, more brown)\n" +
            "    vec3 droughtColor = dayColor * vec3(1.2, 1.0, 0.8);\n" +
            "    dayColor = mix(dayColor, droughtColor, temperatureChange * 0.2);\n" +
            "    \n" +
            "    // Seasonal variation\n" +
            "    float latitudeFactor = abs(uv.y - 0.5) * 2.0; // 0 at equator, 1 at poles\n" +
            "    if (latitudeFactor > 0.3) { // Only apply to non-tropical regions\n" +
            "        vec3 autumnColor = dayColor * vec3(1.3, 1.1, 0.7);\n" +
            "        dayColor = mix(dayColor, autumnColor, seasonalVariation * 0.3 * latitudeFactor);\n" +
            "    }\n" +
            "    \n" +
            "    return dayColor;\n" +
            "}\n" +
            "\n" +
            "// Enhanced ice cap rendering\n" +
            "vec3 applyIceCaps(vec3 color, vec2 uv) {\n" +
            "    // Arctic ice cap (reduced by climate change)\n" +
            "    float arcticThreshold = 0.12 + iceCapReduction * 0.08;\n" +
            "    if (uv.y < arcticThreshold) {\n" +
            "        float iceStrength = (iceCapsIntensity - iceCapReduction) * smoothstep(arcticThreshold, 0.08, uv.y);\n"
            +
            "        vec3 meltingIce = mix(iceColor, vec3(0.6, 0.7, 0.8), iceCapReduction * 0.5);\n" +
            "        color = mix(color, meltingIce * polarBrightness, max(iceStrength, 0.0));\n" +
            "    }\n" +
            "    \n" +
            "    // Antarctic ice cap (also reduced by climate change)\n" +
            "    float antarcticThreshold = 0.88 - iceCapReduction * 0.06;\n" +
            "    if (uv.y > antarcticThreshold) {\n" +
            "        float iceStrength = (iceCapsIntensity - iceCapReduction) * smoothstep(antarcticThreshold, 0.92, uv.y);\n"
            +
            "        vec3 meltingIce = mix(iceColor, vec3(0.6, 0.7, 0.8), iceCapReduction * 0.5);\n" +
            "        color = mix(color, meltingIce * polarBrightness, max(iceStrength, 0.0));\n" +
            "    }\n" +
            "    \n" +
            "    // Greenland ice sheet (show melting)\n" +
            "    if (uv.x >= 0.35 && uv.x <= 0.45 && uv.y >= 0.15 && uv.y <= 0.25) {\n" +
            "        float greenlandIce = (iceCapsIntensity - iceCapReduction * 1.2) * 0.8;\n" +
            "        if (greenlandIce > 0.0) {\n" +
            "            vec3 meltingGreenlandIce = mix(iceColor, vec3(0.4, 0.5, 0.6), iceCapReduction * 0.8);\n" +
            "            color = mix(color, meltingGreenlandIce * 0.9, greenlandIce);\n" +
            "        }\n" +
            "    }\n" +
            "    \n" +
            "    return color;\n" +
            "}\n" +
            "\n" +
            "void main() {\n" +
            "    if (atmospherePass > 0.5) {\n" +
            "        // Atmosphere rendering\n" +
            "        vec3 norm = normalize(Normal);\n" +
            "        vec3 sunDir = normalize(sunPosition - FragPos);\n" +
            "        float fresnel = 1.0 - max(0.0, dot(norm, ViewDir));\n" +
            "        \n" +
            "        // Enhanced atmospheric scattering with pollution effects\n" +
            "        vec3 atmColor = scatteringCoeff * atmosphereDensity;\n" +
            "        atmColor = mix(atmColor, atmColor * vec3(1.2, 0.9, 0.7), pollutionLevel * 0.4);\n" +
            "        \n" +
            "        FragColor = vec4(atmColor, fresnel * 0.3);\n" +
            "        return;\n" +
            "    }\n" +
            "    \n" +
            "    // Get enhanced normal from normal map\n" +
            "    vec3 norm = getNormalFromMap(TexCoord);\n" +
            "    vec3 sunDir = normalize(sunPosition - FragPos);\n" +
            "    \n" +
            "    // Lighting calculations\n" +
            "    float dotLightSun = dot(norm, sunDir);\n" +
            "    vec3 ambient = ambientColor * climateIntensity;\n" +
            "    vec3 diffuse = sunColor * max(dotLightSun, 0.0) * sunIntensity;\n" +
            "    \n" +
            "    // Determine if this is water or land using specular map\n" +
            "    float specularMask = texture(specularTexture, TexCoord).r;\n" +
            "    bool isWater = specularMask > 0.5;\n" +
            "    \n" +
            "    vec3 earthColor;\n" +
            "    \n" +
            "    if (isWater) {\n" +
            "        // Enhanced ocean rendering\n" +
            "        earthColor = getOceanColor(TexCoord, norm, sunDir, ViewDir, dotLightSun);\n" +
            "    } else {\n" +
            "        // Enhanced land rendering with climate effects\n" +
            "        earthColor = getLandColor(TexCoord);\n" +
            "        \n" +
            "        // Apply ice caps\n" +
            "        earthColor = applyIceCaps(earthColor, TexCoord);\n" +
            "    }\n" +
            "    \n" +
            "    // Advanced cloud rendering\n" +
            "    float cloudCover = getAdvancedClouds(TexCoord);\n" +
            "    \n" +
            "    // Cloud shadows and lighting\n" +
            "    vec3 cloudColor = mix(vec3(0.9, 0.9, 1.0), vec3(0.7, 0.7, 0.8), 1.0 - max(dotLightSun, 0.0));\n" +
            "    earthColor = mix(earthColor, cloudColor, cloudCover * 0.9);\n" +
            "    \n" +
            "    // Apply cloud shadows to surface\n" +
            "    diffuse *= (1.0 - cloudCover * cloudShadowing);\n" +
            "    \n" +
            "    // City lights for night side\n" +
            "    vec3 cityLights = getCityLights(TexCoord, dotLightSun);\n" +
            "    \n" +
            "    // Smooth day/night transition\n" +
            "    float dayNightBlend = smoothstep(-0.3, 0.3, dotLightSun);\n" +
            "    vec3 nightColor = earthColor * nightSideVisibility + cityLights;\n" +
            "    earthColor = mix(nightColor, earthColor, dayNightBlend);\n" +
            "    \n" +
            "    // Apply final lighting\n" +
            "    vec3 finalColor = earthColor * (ambient + diffuse);\n" +
            "    \n" +
            "    // Atmospheric perspective and scattering\n" +
            "    float atmosphereEffect = 1.0 + atmosphereDensity * 0.2;\n" +
            "    finalColor *= atmosphereEffect;\n" +
            "    \n" +
            "    // Enhanced limb darkening for realism\n" +
            "    float limb = dot(normalize(Normal), ViewDir);\n" +
            "    float limbDarkening = smoothstep(0.0, 0.6, limb);\n" +
            "    finalColor = mix(finalColor * 0.6, finalColor, limbDarkening);\n" +
            "    \n" +
            "    // Apply global climate tinting\n" +
            "    if (temperatureChange > 0.5) {\n" +
            "        // Warmer, more reddish tint for global warming\n" +
            "        finalColor = mix(finalColor, finalColor * vec3(1.1, 1.0, 0.9), temperatureChange * 0.1);\n" +
            "    }\n" +
            "    \n" +
            "    // Apply pollution haze\n" +
            "    if (pollutionLevel > 0.2) {\n" +
            "        vec3 hazeColor = vec3(0.8, 0.7, 0.6);\n" +
            "        finalColor = mix(finalColor, hazeColor, pollutionLevel * 0.15);\n" +
            "    }\n" +
            "    \n" +
            "    // Gamma correction for realistic appearance\n" +
            "    finalColor = pow(finalColor, vec3(1.0/2.2));\n" +
            "    \n" +
            "    FragColor = vec4(finalColor, 1.0);\n" +
            "}";

    public GlobeShader() {
        createShaderProgram();
        System.out.println("Enhanced Earth shader with real satellite texture support compiled successfully");
    }

    private void createShaderProgram() {
        // Compile vertex shader
        vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, VERTEX_SHADER_SOURCE);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        // Compile fragment shader
        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, FRAGMENT_SHADER_SOURCE);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        // Create shader program
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);
        checkCompileErrors(shaderProgram, "PROGRAM");

        // Delete individual shaders
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void checkCompileErrors(int shader, String type) {
        int success;
        String infoLog;

        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == GL_FALSE) {
                infoLog = glGetProgramInfoLog(shader);
                System.err.println("ERROR: Shader program linking failed: " + infoLog);
                throw new RuntimeException("Shader linking failed: " + type);
            }

            glValidateProgram(shader);
            success = glGetProgrami(shader, GL_VALIDATE_STATUS);
            if (success == GL_FALSE) {
                infoLog = glGetProgramInfoLog(shader);
                System.err.println("WARNING: Shader program validation failed: " + infoLog);
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == GL_FALSE) {
                infoLog = glGetShaderInfoLog(shader);
                System.err.println("ERROR: Shader compilation failed (" + type + "): " + infoLog);
                throw new RuntimeException("Shader compilation failed: " + type);
            }
        }
    }

    public void setMatrix4f(String name, Matrix4f matrix) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                FloatBuffer buffer = stack.mallocFloat(16);
                matrix.get(buffer);
                glUniformMatrix4fv(location, false, buffer);
            }
        }
    }

    public void setFloat(String name, float value) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            glUniform1f(location, value);
        }
    }

    public void setVec3(String name, float x, float y, float z) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            glUniform3f(location, x, y, z);
        }
    }

    public void setVector3f(String name, Vector3f vector) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            glUniform3f(location, vector.x, vector.y, vector.z);
        }
    }

    public void setVector2f(String name, Vector2f vector) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            glUniform2f(location, vector.x, vector.y);
        }
    }

    public void setInt(String name, int value) {
        int location = glGetUniformLocation(shaderProgram, name);
        if (location != -1) {
            glUniform1i(location, value);
        }
    }

    public void cleanup() {
        glDeleteProgram(shaderProgram);
        System.out.println("Enhanced Earth shader cleaned up");
    }

    public void use() {
        glUseProgram(shaderProgram);
    }

    public void unbind() {
        glUseProgram(0);
    }
}