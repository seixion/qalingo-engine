/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.core.web.mvc.viewbean;

import java.util.ArrayList;
import java.util.List;

public class OrderItemViewBean extends AbstractViewBean {

    /**
     * Generated UID
     */
    private static final long serialVersionUID = -1319651665404107265L;

    protected String skuCode;

    protected String i18nName;
    protected String i18nDescription;
    
    protected int quantity;
    protected String price;
    protected String amount;

    private List<OrderShippingViewBean> orderShippings = new ArrayList<OrderShippingViewBean>();
    private List<OrderTaxViewBean> orderTaxes = new ArrayList<OrderTaxViewBean>();

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getI18nName() {
        return i18nName;
    }
    
    public void setI18nName(String i18nName) {
        this.i18nName = i18nName;
    }
    
    public String getI18nDescription() {
        return i18nDescription;
    }

    public void setI18nDescription(String i18nDescription) {
        this.i18nDescription = i18nDescription;
    }
    
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<OrderShippingViewBean> getOrderShippings() {
        return orderShippings;
    }

    public void setOrderShippings(List<OrderShippingViewBean> orderShippings) {
        this.orderShippings = orderShippings;
    }

    public List<OrderTaxViewBean> getOrderTaxes() {
        return orderTaxes;
    }

    public void setOrderTaxes(List<OrderTaxViewBean> orderTaxes) {
        this.orderTaxes = orderTaxes;
    }

}