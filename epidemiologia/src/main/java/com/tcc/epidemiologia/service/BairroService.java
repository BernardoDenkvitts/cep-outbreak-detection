package com.tcc.epidemiologia.service;

import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.features.user.FeatureResultSet;
import mil.nga.geopackage.features.user.FeatureRow;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.index.strtree.STRtree;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BairroService {

    private final Map<Long, String> codigoToNome = new ConcurrentHashMap<>();
    private final STRtree index = new STRtree();
    private final GeometryFactory geometryFactory;

    public BairroService(FeatureDao dao,
                         GeometryFactory geometryFactory,
                         WKBReader wkbReader) {
        this.geometryFactory = geometryFactory;
        FeatureResultSet rs = dao.queryForAll();
        try {
            while (rs.moveToNext()) {
                FeatureRow row = rs.getRow();
                byte[] wkb = row.getGeometry().getWkb();
                Geometry geom = wkbReader.read(wkb);
                long codigo = Long.parseLong(row.getValue("CD_SUBDIST").toString());
                String nome = row.getValue("NM_SUBDIST").toString();
                Bairro bairro = new Bairro(codigo, nome, geom);
                codigoToNome.put(codigo, nome);
                index.insert(geom.getEnvelopeInternal(), bairro);
            }
            index.build();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retorna o bairro da cidade de Passo Fundo referente à latitude e longitude especificadas.
     *
     * <p><strong>Bairros possíveis:</strong></p>
     * <ul>
     *   <li>Região do Bairro Vila Luíza</li>
     *   <li>Região do Bairro Santa Marta</li>
     *   <li>Região do Bairro Nenê Graeff</li>
     *   <li>Região do Bairro Roselândia</li>
     *   <li>Região do Bairro Vila Cruzeiro</li>
     *   <li>Região do Bairro Annes (Vila Fátima e Vila Annes)</li>
     *   <li>Região do Bairro Planaltina</li>
     *   <li>Região do Bairro São Luiz Gonzaga</li>
     *   <li>Região do Bairro São José</li>
     *   <li>Região do Bairro São Cristóvão</li>
     *   <li>Região do Bairro Vera Cruz</li>
     *   <li>Região do Bairro Vila Santa Maria</li>
     *   <li>Região do Bairro Valinhos Loteamento Industrial</li>
     *   <li>Região do Bairro Petrópolis</li>
     *   <li>Região do Bairro Centro (Centro e Vila Vergueiro)</li>
     *   <li>Região do Bairro Vila Mattos</li>
     *   <li>Região do Bairro Integração</li>
     *   <li>Região do Bairro Boqueirão</li>
     *   <li>Região do Bairro Vila Vitor Issler</li>
     *   <li>Região do Bairro Lucas de Araújo</li>
     *   <li>Região do Bairro José Alexandre Zachia</li>
     *   <li>Região do Bairro Vila Rodrigues</li>
     * </ul>
     */
    public Bairro buscar(double latitude, double longitude) {
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));

        // reduz candidatos via envelope (ziper espacial)
        @SuppressWarnings("unchecked")
        List<Bairro> candidatos = index.query(ponto.getEnvelopeInternal());

        return candidatos.stream()
                .filter(bairro -> bairro.getGeometry().contains(ponto))
                .findFirst()
                .orElse(null);
    }

    public List<Long> getAllCodigos() {
        return new ArrayList<>(codigoToNome.keySet());
    }

    public String getNomeByCodigo(long codigo) {
        return codigoToNome.get(codigo);
    }

    public static class Bairro {
        private final long codigo;
        private final String nome;
        public final Geometry geometry;

        public Bairro(long codigo, String nome, Geometry geometry) {
            this.codigo = codigo;
            this.nome = nome;
            this.geometry = geometry;
        }

        public long getCodigo() {
            return codigo;
        }

        public String getNome() {
            return nome;
        }

        public Geometry getGeometry() {
            return geometry;
        }
    }
}
