package com.tcc.epidemiologia.config;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.features.user.FeatureDao;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.WKBReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class GeoToolsConfig {

    @Value("${geotools.geopackage.path}")
    private String geopackagePath;

    @Bean
    public FeatureDao featureDao() {
        File file = new File(geopackagePath);
        GeoPackage gp = GeoPackageManager.open(file);
        return gp.getFeatureDao(gp.getFeatureTables().getFirst());
    }

    @Bean
    public GeometryFactory geometryFactory() {
        return new GeometryFactory();
    }

    @Bean
    public WKBReader wkbReader() {
        return new WKBReader();
    }
}
