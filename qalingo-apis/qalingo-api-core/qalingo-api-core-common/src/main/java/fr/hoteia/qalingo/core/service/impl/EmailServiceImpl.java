/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.core.service.impl;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.VelocityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.velocity.VelocityEngineUtils;

import fr.hoteia.qalingo.core.dao.EmailDao;
import fr.hoteia.qalingo.core.domain.Customer;
import fr.hoteia.qalingo.core.domain.Email;
import fr.hoteia.qalingo.core.domain.Localization;
import fr.hoteia.qalingo.core.email.bean.ContactUsEmailBean;
import fr.hoteia.qalingo.core.email.bean.CustomerForgottenPasswordEmailBean;
import fr.hoteia.qalingo.core.email.bean.CustomerNewAccountConfirmationEmailBean;
import fr.hoteia.qalingo.core.email.bean.NewsletterRegistrationConfirmationEmailBean;
import fr.hoteia.qalingo.core.email.bean.OrderConfirmationEmailBean;
import fr.hoteia.qalingo.core.email.bean.OrderSentConfirmationEmailBean;
import fr.hoteia.qalingo.core.i18n.message.CoreMessageSource;
import fr.hoteia.qalingo.core.service.EmailService;
import fr.hoteia.qalingo.core.util.impl.MimeMessagePreparatorImpl;

@Service("emailService")
@Transactional
public class EmailServiceImpl implements EmailService {

    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
    private EmailDao emailDao;

	@Autowired
    private VelocityEngine velocityEngine;
    
	@Autowired
    private MimeMessagePreparatorImpl mimeMessagePreparator;
    
	@Autowired
	protected CoreMessageSource coreMessageSource;
	
	public Email getEmailById(Long id) {
		return emailDao.getEmailById(id);
	}

	public List<Email> findEmailByStatus(String status) {
		return emailDao.findEmailByStatus(status);
	}
	
	public List<Long> findIdsForEmailSync() {
		return emailDao.findIdsForEmailSync();
	}
	
	public void saveOrUpdateEmail(Email email) {
		emailDao.saveOrUpdateEmail(email);
	}
	
	public void deleteEmail(Email email) {
		emailDao.deleteEmail(email);
	}
	
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#buildAndSaveContactUsMail(Localization localization, Customer customer, String VelocityPath, ContactUsEmailBean contactUsEmailBean)
     */
    public void buildAndSaveContactUsMail(final Localization localization, final Customer customer, final String VelocityPath, final ContactUsEmailBean contactUsEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
        	String fromEmail = contactUsEmailBean.getEmail();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("contactUsEmailBean", contactUsEmailBean);

        	String toEmail = contactUsEmailBean.getToEmail();
        	mimeMessagePreparator.setTo(toEmail);
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {contactUsEmailBean.getLastname(), contactUsEmailBean.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("contact.us.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "contact-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "contact-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_CONTACT_US);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);
        	
        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }
    
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#saveAndBuildNewsletterRegistrationConfirmationMail(Localization localization, Customer customer, String VelocityPath, NewsletterRegistrationConfirmationEmailBean newsletterRegistrationConfirmationEmailBean)
     */
    public void saveAndBuildNewsletterRegistrationConfirmationMail(final Localization localization, final Customer customer, final String VelocityPath, final NewsletterRegistrationConfirmationEmailBean newsletterRegistrationConfirmationEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("newsletterRegistrationConfirmationEmailBean", newsletterRegistrationConfirmationEmailBean);

        	String fromEmail = newsletterRegistrationConfirmationEmailBean.getFromEmail();
        	mimeMessagePreparator.setTo(customer.getEmail());
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {customer.getLastname(), customer.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("newsletter.registration.confirmation.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "newsletter-registration-confirmation-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "newsletter-registration-confirmation-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_NEWSLETTER_REGISTRATION_CONFIRMATION);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);

        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }
    
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#buildAndSaveCustomerNewAccountMail(Localization localization, Customer customer, String VelocityPath, CustomerNewAccountConfirmationEmailBean customerNewAccountConfirmationEmailBean)
     */
    public void buildAndSaveCustomerNewAccountMail(final Localization localization, final Customer customer, final String VelocityPath, final CustomerNewAccountConfirmationEmailBean customerNewAccountConfirmationEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("customerNewAccountConfirmationEmailBean", customerNewAccountConfirmationEmailBean);

        	String fromEmail = customerNewAccountConfirmationEmailBean.getFromEmail();
        	mimeMessagePreparator.setTo(customer.getEmail());
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {customer.getLastname(), customer.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("customer.new.account.confirmation.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "new-account-confirmation-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "new-account-confirmation-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_NEW_ACCOUNT_CONFIRMATION);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);
        	
        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }
    
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#buildAndSaveCustomerForgottenPasswordMail(Localization localization, Customer customer, String VelocityPath, CustomerForgottenPasswordEmailBean customerForgottenPasswordEmailBean)
     */
    public void buildAndSaveCustomerForgottenPasswordMail(final Localization localization, final Customer customer, final String VelocityPath, final CustomerForgottenPasswordEmailBean customerForgottenPasswordEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("customerForgottenPasswordEmailBean", customerForgottenPasswordEmailBean);

        	String fromEmail = customerForgottenPasswordEmailBean.getFromEmail();
        	mimeMessagePreparator.setTo(customer.getEmail());
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {customer.getLastname(), customer.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("customer.forgotten.password.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotten-password-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "forgotten-password-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_FORGOTTEN_PASSWORD);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);
        	
        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }
    
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#buildAndSaveNewOrderConfirmationMail(Localization localization, Customer customer, String VelocityPath, OrderConfirmationEmailBean orderConfirmationEmailBean)
     */
    public void buildAndSaveNewOrderConfirmationMail(final Localization localization, final Customer customer, final String VelocityPath, final OrderConfirmationEmailBean orderConfirmationEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("orderConfirmationEmailBean", orderConfirmationEmailBean);

        	String fromEmail = orderConfirmationEmailBean.getFromEmail();
        	mimeMessagePreparator.setTo(customer.getEmail());
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {customer.getLastname(), customer.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("order.confirmation.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "order-confirmation-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "order-confirmation-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_ORDER_CONFIRMATION);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);
        	
        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }
    
    /**
     * @see fr.hoteia.qalingo.core.service.EmailService#buildAndSaveOrderSentConfirmationMail(Localization localization, Customer customer, String VelocityPath, OrderSentConfirmationEmailBean orderSentConfirmationEmailBean)
     */
    public void buildAndSaveOrderSentConfirmationMail(final Localization localization, final Customer customer, final String VelocityPath, final OrderSentConfirmationEmailBean orderSentConfirmationEmailBean) {
        try {
        	final Locale locale = localization.getLocale();
        	
        	Map<String, Object> model = new HashMap<String, Object>();
          
        	DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL, locale);
        	java.sql.Timestamp currentDate = new java.sql.Timestamp((new java.util.Date()).getTime());
        	model.put("currentDate", dateFormatter.format(currentDate));
        	model.put("customer", customer);
        	model.put("orderSentConfirmationEmailBean", orderSentConfirmationEmailBean);

        	String fromEmail = orderSentConfirmationEmailBean.getFromEmail();
        	mimeMessagePreparator.setTo(customer.getEmail());
        	mimeMessagePreparator.setFrom(fromEmail);
        	mimeMessagePreparator.setReplyTo(fromEmail);
        	Object[] parameters = {customer.getLastname(), customer.getFirstname()};
        	mimeMessagePreparator.setSubject(coreMessageSource.getMessage("order.sent.confirmation.email.prospect.subject", parameters, locale));
        	mimeMessagePreparator.setHtmlContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "order-sent-html-content.vm", model));
        	mimeMessagePreparator.setPlainTextContent(VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "order-sent-text-content.vm", model));
        	
        	Email email = new Email();
        	email.setType(Email.EMAIl_TYPE_ORDER_SENT);
        	email.setStatus(Email.EMAIl_STATUS_PENDING);
        	
        	saveOrUpdateEmail(email);
        	
        } catch (MailException e) {
        	LOG.error("Error, can't save the message :", e);
        } catch (VelocityException e) {
        	LOG.error("Error, can't build the message :", e);
        }
    }

}