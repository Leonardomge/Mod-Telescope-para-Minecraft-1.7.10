package com.modtelescopio.client.model;

/**
 * Destino dos quadriláteros gerados pelo {@link ModelBuilder}. Cada versão do Minecraft
 * implementa isto com o seu próprio sistema de renderização (Tessellator no 1.7.10,
 * VertexConsumer no 1.20.1).
 */
public interface QuadSink {
    /**
     * Recebe um quadrilátero já transformado. Os 4 vértices vêm em ordem anti-horária
     * quando vistos pelo lado de fora; as normais já estão normalizadas.
     */
    void quad(float[] x, float[] y, float[] z, float[] nx, float[] ny, float[] nz, int rgb);
}
