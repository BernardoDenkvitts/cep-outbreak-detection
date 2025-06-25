package com.tcc.epidemiologia;

import mil.nga.geopackage.GeoPackage;
import mil.nga.geopackage.GeoPackageManager;
import mil.nga.geopackage.features.user.FeatureDao;
import mil.nga.geopackage.srs.SpatialReferenceSystem;
import mil.nga.geopackage.srs.SpatialReferenceSystemDao;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class EpidemiologiaApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EpidemiologiaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
