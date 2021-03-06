/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.rest.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.cxf.rs.security.cors.CrossOriginResourceSharing;
import org.hoteia.qalingo.core.pojo.LocalizationPojo;
import org.hoteia.qalingo.core.pojo.catalog.CatalogCategoryPojo;
import org.hoteia.qalingo.core.pojo.catalog.CatalogPojo;
import org.hoteia.qalingo.core.pojo.cms.CmsCategoriesPojo;
import org.hoteia.qalingo.core.pojo.cms.CmsContextPojo;
import org.hoteia.qalingo.core.pojo.cms.CmsProductsPojo;
import org.hoteia.qalingo.core.pojo.market.MarketAreaPojo;
import org.hoteia.qalingo.core.pojo.market.MarketPlacePojo;
import org.hoteia.qalingo.core.pojo.market.MarketPojo;
import org.hoteia.qalingo.core.pojo.product.ProductMarketingPojo;
import org.hoteia.qalingo.core.pojo.retailer.RetailerPojo;
import org.hoteia.qalingo.core.service.pojo.LocalizationPojoService;
import org.hoteia.qalingo.core.service.pojo.MarketPojoService;
import org.hoteia.qalingo.core.service.pojo.RetailerPojoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/cms/")
@CrossOriginResourceSharing(allowAllOrigins = true)
@Component("cmsRestService")
public class CmsRestService {

    @Autowired
    private MarketPojoService marketPojoService;

    @Autowired
    private RetailerPojoService retailerPojoService;

    @Autowired
    private LocalizationPojoService localizationPojoService;
    
    @GET
    @Path("marketplaces")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo getMarketPlaces() {
        // DEFAULT CALLBACK WITH ALL THE MARKET PLACES AND DEFAULT MASTER VALUE FOR MARKET / MARKET AREA / RETAILER / LOCALIZATION
        CmsContextPojo cmsContext = new CmsContextPojo();

        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, null);

        // MARKET LIST
        buildMarket(cmsContext, null, null);

        // MARKET AREA LIST
        buildMarketArea(cmsContext, null, null);

        // RETAILER LIST
        buildRetailer(cmsContext, null, null);

        // LOCALIZATION LIST
        buildLocalization(cmsContext, null, null);

        return cmsContext;
    }

    @GET
    @Path("marketplace/set/{marketPlaceCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo selectMarketPlace(@PathParam("marketPlaceCode") final String marketPlaceCode) {
        CmsContextPojo cmsContext = new CmsContextPojo();
        
        MarketPlacePojo selectedMarketPlace = marketPojoService.getMarketPlaceByCode(marketPlaceCode);
        
        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, selectedMarketPlace);
        
        // MARKET LIST
        buildMarket(cmsContext, selectedMarketPlace, null);

        // MARKET AREA LIST
        buildMarketArea(cmsContext, null, null);

        // RETAILER LIST
        buildRetailer(cmsContext, null, null);

        // LOCALIZATION LIST
        buildLocalization(cmsContext, null, null);

        return cmsContext;
    }
    
    @GET
    @Path("market/set/{marketCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo selectMarket(@PathParam("marketCode") final String marketCode) {
        CmsContextPojo cmsContext = new CmsContextPojo();

        MarketPojo selectedMarket = marketPojoService.getMarketByCode(marketCode);

        MarketPlacePojo selectedMarketPlace = marketPojoService.getMarketPlaceByCode(selectedMarket.getMarketPlace().getCode());

        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, selectedMarketPlace);
        
        // MARKET LIST
        buildMarket(cmsContext, selectedMarketPlace, selectedMarket);
        
        // MARKET AREA LIST
        buildMarketArea(cmsContext, selectedMarket, null);

        // RETAILER LIST
        buildRetailer(cmsContext, null, null);

        // LOCALIZATION LIST
        buildLocalization(cmsContext, null, null);

        return cmsContext;
    }
    
    @GET
    @Path("marketarea/set/{marketAreaCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo selectMarketArea(@PathParam("marketAreaCode") final String marketAreaCode) {
        CmsContextPojo cmsContext = new CmsContextPojo();

        MarketAreaPojo selectedMarketArea = marketPojoService.getMarketAreaByCode(marketAreaCode);

        MarketPojo selectedMarket = marketPojoService.getMarketByCode(selectedMarketArea.getMarket().getCode());

        MarketPlacePojo selectedMarketPlace = marketPojoService.getMarketPlaceByCode(selectedMarket.getMarketPlace().getCode());

        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, selectedMarketPlace);
        
        // MARKET LIST
        buildMarket(cmsContext, selectedMarketPlace, selectedMarket);
        
        // MARKET AREA LIST
        buildMarketArea(cmsContext, selectedMarket, selectedMarketArea);

        // RETAILER LIST
        buildRetailer(cmsContext, selectedMarketArea, null);

        // LOCALIZATION LIST
        buildLocalization(cmsContext, selectedMarketArea, null);
        
        return cmsContext;
    }
    
    @GET
    @Path("retailer/set/{marketAreaCode}/{retailerCode}/{localizationCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo selectRetailer(@PathParam("marketAreaCode") final String marketAreaCode, @PathParam("retailerCode") final String retailerCode,
                                     @PathParam("localizationCode") final String localizationCode) {
        CmsContextPojo cmsContext = new CmsContextPojo();

        LocalizationPojo selectedLocalization = localizationPojoService.getLocalizationByCode(localizationCode);

        RetailerPojo selectedRetailer = retailerPojoService.getRetailerByCode(retailerCode);
        
        MarketAreaPojo selectedMarketArea = marketPojoService.getMarketAreaByCode(marketAreaCode);

        MarketPojo selectedMarket = marketPojoService.getMarketByCode(selectedMarketArea.getMarket().getCode());

        MarketPlacePojo selectedMarketPlace = marketPojoService.getMarketPlaceByCode(selectedMarket.getMarketPlace().getCode());

        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, selectedMarketPlace);
        
        // MARKET LIST
        buildMarket(cmsContext, selectedMarketPlace, selectedMarket);
        
        // MARKET AREA LIST
        buildMarketArea(cmsContext, selectedMarket, selectedMarketArea);

        // RETAILER LIST
        buildRetailer(cmsContext, selectedMarketArea, selectedRetailer);

        // LOCALIZATION LIST
        buildLocalization(cmsContext, selectedMarketArea, selectedLocalization);
        
        return cmsContext;
    }
    
    @GET
    @Path("localization/set/{marketAreaCode}/{retailerCode}/{localizationCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsContextPojo selectLocalization(@PathParam("marketAreaCode") final String marketAreaCode, @PathParam("retailerCode") final String retailerCode, 
                                         @PathParam("localizationCode") final String localizationCode) {
        CmsContextPojo cmsContext = new CmsContextPojo();

        LocalizationPojo selectedLocalization = localizationPojoService.getLocalizationByCode(localizationCode);

        RetailerPojo selectedRetailer = retailerPojoService.getRetailerByCode(retailerCode);

        MarketAreaPojo selectedMarketArea = marketPojoService.getMarketAreaByCode(marketAreaCode);

        MarketPojo selectedMarket = marketPojoService.getMarketByCode(selectedMarketArea.getMarket().getCode());

        MarketPlacePojo selectedMarketPlace = marketPojoService.getMarketPlaceByCode(selectedMarket.getMarketPlace().getCode());

        // MARKET PLACE LIST
        buildMarketPlace(cmsContext, selectedMarketPlace);
        
        // MARKET LIST
        buildMarket(cmsContext, selectedMarketPlace, selectedMarket);
        
        // MARKET AREA LIST
        buildMarketArea(cmsContext, selectedMarket, selectedMarketArea);

        // RETAILER LIST
        buildRetailer(cmsContext, selectedMarketArea, selectedRetailer);
        
        // LOCALIZATION LIST
        buildLocalization(cmsContext, selectedMarketArea, selectedLocalization);
        
        return cmsContext;
    }
    
    @GET
    @Path("catalog/categories/{marketAreaCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsCategoriesPojo categoriesByMarketArea(@PathParam("marketAreaCode") final String marketAreaCode) {
        CmsCategoriesPojo cmsCategories = new CmsCategoriesPojo();

        MarketAreaPojo selectedMarketArea = marketPojoService.getMarketAreaByCode(marketAreaCode);
        if(selectedMarketArea != null){
            selectedMarketArea.setMarket(null);
            selectedMarketArea.setRetailers(null);
            selectedMarketArea.setLocalizations(null);
            
            cmsCategories.setMarketArea(selectedMarketArea);
            List<CatalogCategoryPojo> categories = selectedMarketArea.getCatalog().getSortedRootCatalogCategories();
            for (Iterator<CatalogCategoryPojo> iterator = categories.iterator(); iterator.hasNext();) {
                CatalogCategoryPojo catalogCategoryPojo = (CatalogCategoryPojo) iterator.next();
                catalogCategoryPojo.setCatalogCategoryGlobalAttributes(null);
                catalogCategoryPojo.setCatalogCategoryMarketAreaAttributes(null);
                catalogCategoryPojo.setProductMarketings(null);
            }
            cmsCategories.setCatalogCategories(categories);
            
            CatalogPojo catalog = selectedMarketArea.getCatalog();
            catalog.setSortedRootCatalogCategories(null);
            catalog.setSortedAllCatalogCategories(null);
            cmsCategories.setCatalog(catalog);
        }

        return cmsCategories;
    }
    
    @GET
    @Path("catalog/products/{marketAreaCode}/{categoryCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public CmsProductsPojo productsByMarketArea(@PathParam("marketAreaCode") final String marketAreaCode, @PathParam("categoryCode") final String categoryCode) {
        CmsProductsPojo cmsProducts = new CmsProductsPojo();

        MarketAreaPojo selectedMarketArea = marketPojoService.getMarketAreaByCode(marketAreaCode);
        if(selectedMarketArea != null){
            selectedMarketArea.setMarket(null);
            selectedMarketArea.setRetailers(null);
            selectedMarketArea.setLocalizations(null);
            
            cmsProducts.setMarketArea(selectedMarketArea);
            
            List<CatalogCategoryPojo> categories = selectedMarketArea.getCatalog().getSortedRootCatalogCategories();
            for (Iterator<CatalogCategoryPojo> iterator = categories.iterator(); iterator.hasNext();) {
                CatalogCategoryPojo catalogCategoryPojo = (CatalogCategoryPojo) iterator.next();
                if(catalogCategoryPojo.getCode().equals(categoryCode)){
                    List<ProductMarketingPojo> products = catalogCategoryPojo.getProductMarketings();
                    for (Iterator<ProductMarketingPojo> iteratorProductMarketingPojo = products.iterator(); iteratorProductMarketingPojo.hasNext();) {
                        ProductMarketingPojo productMarketingPojo = (ProductMarketingPojo) iteratorProductMarketingPojo.next();
                        productMarketingPojo.setProductBrand(null);
                        productMarketingPojo.setProductMarketingMarketAreaAttributes(null);
                        productMarketingPojo.setProductMarketingGlobalAttributes(null);
                        productMarketingPojo.setProductSkus(null);
                        productMarketingPojo.setProductAssociationLinks(null);
                    }
                    cmsProducts.setProductMarketings(products);
                }
            }

            CatalogPojo catalog = selectedMarketArea.getCatalog();
            catalog.setSortedRootCatalogCategories(null);
            cmsProducts.setCatalog(catalog);

        }

        return cmsProducts;
    }
    
    private void buildMarketPlace(CmsContextPojo cmsContext, MarketPlacePojo selectedMarketPlace){
        List<MarketPlacePojo> marketPlaces = new ArrayList<MarketPlacePojo>();
        MarketPlacePojo masterMarketPlace = new MarketPlacePojo();
        masterMarketPlace.setCode("MASTER_MARKETPLACE");
        masterMarketPlace.setName("Master MarketPlace");
        if(selectedMarketPlace == null){
            masterMarketPlace.setSelected(true);
        }
        marketPlaces.add(masterMarketPlace);
        
        // HACK TEMPORARY BEFORE GOOD FETCH STATEGY
        List<MarketPlacePojo> allMarketPlaces = marketPojoService.getMarketPlaces();
        for (Iterator<MarketPlacePojo> iterator = allMarketPlaces.iterator(); iterator.hasNext();) {
            MarketPlacePojo marketPlacePojo = (MarketPlacePojo) iterator.next();
            if(selectedMarketPlace != null
                    && marketPlacePojo.getCode().equals(selectedMarketPlace.getCode())){
                marketPlacePojo.setSelected(true);
                selectedMarketPlace = marketPlacePojo;
            }
            marketPlacePojo.setMarkets(null);
        }
        marketPlaces.addAll(allMarketPlaces);
        
        cmsContext.setMarketPlaces(marketPlaces);
    }

    private void buildMarket(CmsContextPojo cmsContext, MarketPlacePojo selectedMarketPlace, MarketPojo selectedMarket){
        List<MarketPojo> markets = new ArrayList<MarketPojo>();
        MarketPojo masterMarket = new MarketPojo();
        masterMarket.setCode("MASTER_MARKET");
        masterMarket.setName("Master Market");
        if(selectedMarket == null){
            masterMarket.setSelected(true);
        }
        markets.add(masterMarket);
        
        if(selectedMarketPlace != null){
            for (Iterator<MarketPojo> iterator = selectedMarketPlace.getMarkets().iterator(); iterator.hasNext();) {
                MarketPojo marketPojo = (MarketPojo) iterator.next();
                MarketPojo market = marketPojoService.getMarketByCode(marketPojo.getCode());
                if(selectedMarket != null
                        && market.getCode().equals(selectedMarket.getCode())){
                    market.setSelected(true);
                }
                market.setMarketPlace(null);
                market.setMarketAreas(null);
                markets.add(market);
            }
        }
        
        cmsContext.setMarkets(markets);
    }
    
    private void buildMarketArea(CmsContextPojo cmsContext, MarketPojo selectedMarket, MarketAreaPojo selectedMarketArea){
        List<MarketAreaPojo> marketAreas = new ArrayList<MarketAreaPojo>();
        MarketAreaPojo masterArea = new MarketAreaPojo();
        masterArea.setCode("MASTER_MARKET_AREA");
        masterArea.setName("Master Market Area");
        if(selectedMarket == null){
            masterArea.setSelected(true);
        }
        marketAreas.add(masterArea);
        
        if(selectedMarket != null){
            for (Iterator<MarketAreaPojo> iterator = selectedMarket.getMarketAreas().iterator(); iterator.hasNext();) {
                MarketAreaPojo marketAreaPojo = (MarketAreaPojo) iterator.next();
                MarketAreaPojo marketArea = marketPojoService.getMarketAreaByCode(marketAreaPojo.getCode());
                if(selectedMarketArea != null
                        && marketArea.getCode().equals(selectedMarketArea.getCode())){
                    marketArea.setSelected(true);
                }
                marketArea.setMarket(null);
                marketArea.setRetailers(null);
                marketArea.setLocalizations(null);
                marketAreas.add(marketArea);
            }
        }
        
        cmsContext.setMarketAreas(marketAreas);
    }
    
    private void buildRetailer(CmsContextPojo cmsContext, MarketAreaPojo selectedMarketArea, RetailerPojo selectedRetailer){
        List<RetailerPojo> retailers = new ArrayList<RetailerPojo>();
        RetailerPojo retailer = new RetailerPojo();
        retailer.setCode("MASTER_RETAILER");
        retailer.setName("Master Retailer");
        if(selectedRetailer == null){
            retailer.setSelected(true);
        }
        retailers.add(retailer);
        
        if(selectedMarketArea != null){
            List<RetailerPojo> retailersByMarketAreaCode = retailerPojoService.findRetailersByMarketAreaCode(selectedMarketArea.getCode());
            for (Iterator<RetailerPojo> iterator = retailersByMarketAreaCode.iterator(); iterator.hasNext();) {
                RetailerPojo retailerPojo = (RetailerPojo) iterator.next();
                if(selectedRetailer != null
                        && retailerPojo.getCode().equals(selectedRetailer.getCode())){
                    retailerPojo.setSelected(true);
                }
                retailerPojo.setCustomerComments(null);
                retailerPojo.setCustomerRates(null);
                retailerPojo.setStores(null);
                retailerPojo.setAddresses(null);
                retailers.add(retailerPojo);
            }
        }
        
        cmsContext.setRetailers(retailers);
    }
    
    private void buildLocalization(CmsContextPojo cmsContext, MarketAreaPojo selectedMarketArea, LocalizationPojo selectedLocalization){
        List<LocalizationPojo> localizations = new ArrayList<LocalizationPojo>();
        LocalizationPojo localization = new LocalizationPojo();
        localization.setCode("MASTER_LOCALIZATION");
        localization.setName("Master Localization");
        if(selectedLocalization == null){
            localization.setSelected(true);
        }
        localizations.add(localization);
        
        if(selectedMarketArea != null){
            List<LocalizationPojo> localizationsByMarketAreaCode = localizationPojoService.findLocalizationsByMarketAreaCode(selectedMarketArea.getCode());
            for (Iterator<LocalizationPojo> iterator = localizationsByMarketAreaCode.iterator(); iterator.hasNext();) {
                LocalizationPojo localizationPojo = (LocalizationPojo) iterator.next();
                if(selectedLocalization != null
                        && localizationPojo.getCode().equals(selectedLocalization.getCode())){
                    localizationPojo.setSelected(true);
                }
                localizations.add(localizationPojo);
            }
        }
        
        cmsContext.setLocalizations(localizations);
    }
    
}