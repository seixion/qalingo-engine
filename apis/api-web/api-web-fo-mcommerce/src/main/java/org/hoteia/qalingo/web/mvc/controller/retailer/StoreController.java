/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.web.mvc.controller.retailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hoteia.qalingo.core.ModelConstants;
import org.hoteia.qalingo.core.RequestConstants;
import org.hoteia.qalingo.core.domain.Localization;
import org.hoteia.qalingo.core.domain.Retailer;
import org.hoteia.qalingo.core.domain.Retailer_;
import org.hoteia.qalingo.core.domain.Store;
import org.hoteia.qalingo.core.domain.Store_;
import org.hoteia.qalingo.core.domain.enumtype.FoUrls;
import org.hoteia.qalingo.core.fetchplan.FetchPlan;
import org.hoteia.qalingo.core.fetchplan.SpecificFetchMode;
import org.hoteia.qalingo.core.i18n.enumtype.ScopeWebMessage;
import org.hoteia.qalingo.core.pojo.RequestData;
import org.hoteia.qalingo.core.service.RetailerService;
import org.hoteia.qalingo.core.web.mvc.viewbean.BreadcrumbViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.MenuViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.RetailerViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.StoreBusinessHourViewBean;
import org.hoteia.qalingo.core.web.mvc.viewbean.StoreViewBean;
import org.hoteia.qalingo.core.web.servlet.ModelAndViewThemeDevice;
import org.hoteia.qalingo.core.web.servlet.view.RedirectView;
import org.hoteia.qalingo.web.mvc.controller.AbstractMCommerceController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 */
@Controller("storeController")
public class StoreController extends AbstractMCommerceController {

	@Autowired
	protected RetailerService retailerService;
	
    protected List<SpecificFetchMode> retailerFetchPlans = new ArrayList<SpecificFetchMode>();
    protected List<SpecificFetchMode> storeFetchPlans = new ArrayList<SpecificFetchMode>();

    public StoreController() {
        retailerFetchPlans.add(new SpecificFetchMode(Retailer_.attributes.getName()));
        retailerFetchPlans.add(new SpecificFetchMode(Retailer_.assets.getName()));
        retailerFetchPlans.add(new SpecificFetchMode(Retailer_.addresses.getName()));
        
        storeFetchPlans.add(new SpecificFetchMode(Store_.retailer.getName()));
        storeFetchPlans.add(new SpecificFetchMode(Store_.attributes.getName()));
        storeFetchPlans.add(new SpecificFetchMode(Store_.assets.getName()));
        storeFetchPlans.add(new SpecificFetchMode(Store_.businessHours.getName()));
    }
    
	@RequestMapping(FoUrls.STORE_DETAILS_URL)
	public ModelAndView displayStoreDetails(final HttpServletRequest request, final Model model, @PathVariable(RequestConstants.URL_PATTERN_STORE_CODE) final String storeCode) throws Exception {
		ModelAndViewThemeDevice modelAndView = new ModelAndViewThemeDevice(getCurrentVelocityPath(request), FoUrls.STORE_DETAILS.getVelocityPage());
        final RequestData requestData = requestUtil.getRequestData(request);
	      
		if(StringUtils.isNotEmpty(storeCode)){
            Store store = retailerService.getStoreByCode(storeCode, new FetchPlan(storeFetchPlans));
            StoreViewBean storeViewBean = frontofficeViewBeanFactory.buildViewBeanStore(requestUtil.getRequestData(request), store);
            model.addAttribute(ModelConstants.STORE_VIEW_BEAN, storeViewBean);

            Retailer retailer = retailerService.getRetailerById(store.getRetailer().getId(), new FetchPlan(retailerFetchPlans));
            RetailerViewBean retailerViewBean = frontofficeViewBeanFactory.buildViewBeanRetailer(requestUtil.getRequestData(request), retailer);
            model.addAttribute(ModelConstants.RETAILER_VIEW_BEAN, retailerViewBean);
            
            StoreBusinessHourViewBean storeBusinessHourViewBean = frontofficeViewBeanFactory.buildViewBeanStoreBusinessHour(store);
            model.addAttribute("businessHours", storeBusinessHourViewBean);

            final List<Store> stores = retailerService.findStores();
            final List<StoreViewBean> otherStoreViewBeans = frontofficeViewBeanFactory.buildListViewBeanStore(requestUtil.getRequestData(request), stores);
            otherStoreViewBeans.remove(storeViewBean);
            model.addAttribute("otherStores", otherStoreViewBeans);
	        
	        model.addAttribute("withMap", true);
	        
	        Object[] params = {storeViewBean.getI18nName()};
	        overrideDefaultMainContentTitle(request, modelAndView, FoUrls.STORE_DETAILS.getKey(), params);

            model.addAttribute(ModelConstants.BREADCRUMB_VIEW_BEAN, buildBreadcrumbViewBean(requestData, store));
	        
	        return modelAndView;
		}
		
        final String urlRedirect = urlService.generateUrl(FoUrls.STORE_LOCATION, requestData);
        return new ModelAndView(new RedirectView(urlRedirect));
	}

    protected BreadcrumbViewBean buildBreadcrumbViewBean(final RequestData requestData, Store store) {
        final Localization localization = requestData.getMarketAreaLocalization();
        final String localizationCode = localization.getCode();
        final Locale locale = requestData.getLocale();
        Object[] params = { store.getI18nName(localizationCode) };

        // BREADCRUMB
        BreadcrumbViewBean breadcrumbViewBean = new BreadcrumbViewBean();
        breadcrumbViewBean.setName(getSpecificMessage(ScopeWebMessage.HEADER_TITLE, "store_details", params, locale));

        List<MenuViewBean> menuViewBeans = new ArrayList<MenuViewBean>();
        MenuViewBean menu = new MenuViewBean();
        menu.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, FoUrls.HOME.getMessageKey(), locale));
        menu.setUrl(urlService.generateUrl(FoUrls.HOME, requestData));
        menuViewBeans.add(menu);

        menu = new MenuViewBean();
        menu.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "store_location", locale));
        menu.setUrl(urlService.generateUrl(FoUrls.STORE_LOCATION, requestData));
        menuViewBeans.add(menu);

        menu = new MenuViewBean();
        menu.setName(getSpecificMessage(ScopeWebMessage.HEADER_MENU, "store_details", params, locale));
        menu.setUrl(urlService.generateUrl(FoUrls.STORE_DETAILS, requestData, store));
        menu.setActive(true);
        menuViewBeans.add(menu);

        breadcrumbViewBean.setMenus(menuViewBeans);
        return breadcrumbViewBean;
    }

}