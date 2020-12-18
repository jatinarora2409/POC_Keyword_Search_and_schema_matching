package com.infa.products.discovery.automatedclassification.model;

import java.util.Objects;
import java.util.Optional;

public class Asset {

    private String catalogId;

    private String name;

    private AssetType assetType;

    private Optional<String> parent = Optional.ofNullable(null);

    public Asset(String catalogId, String name, AssetType assetType) {
        super();
        this.catalogId = catalogId;
        this.name = name;
        this.assetType = assetType;
    }

    public Asset(String catalogId, String name, AssetType assetType, String parent) {
        super();
        this.catalogId = catalogId;
        this.name = name;
        this.assetType = assetType;
        this.parent = Optional.ofNullable(parent);
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public void setParent(String parent) {
        this.parent = Optional.ofNullable(parent);
    }

    public Optional<String> getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(catalogId, asset.catalogId) &&
                Objects.equals(name, asset.name) &&
                assetType == asset.assetType &&
                parent == asset.parent;
    }

    @Override
    public int hashCode() {
        return Objects.hash(catalogId, name, assetType);
    }
}
