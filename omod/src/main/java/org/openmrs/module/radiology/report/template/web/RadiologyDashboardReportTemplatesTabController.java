/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.radiology.report.template.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.openmrs.api.APIException;
import org.openmrs.module.radiology.report.template.MrrtReportTemplateService;
import org.openmrs.module.radiology.web.RadiologyWebConstants;
import org.openmrs.web.WebConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the dashboard tab containing {@code RadiologyReportTemplates}.
 */
@Controller
@RequestMapping(RadiologyDashboardReportTemplatesTabController.RADIOLOGY_REPORT_TEMPLATES_TAB_REQUEST_MAPPING)
public class RadiologyDashboardReportTemplatesTabController {
    
    
    public static final String RADIOLOGY_REPORT_TEMPLATES_TAB_REQUEST_MAPPING =
            "/module/radiology/radiologyDashboardReportTemplatesTab.htm";
    
    static final String RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW = "/module/radiology/radiologyDashboardReportTemplatesTab";
    
    @Autowired
    private MrrtReportTemplateService mrrtReportTemplateService;
    
    /**
     * Handles get requests for radiology report templates tab.
     * 
     * @return model and view of the radiology report templates tab page
     * @should return model and view of the radiology report templates tab page and set tab session attribute to radiology reports tab page
     */
    @RequestMapping(method = RequestMethod.GET)
    protected ModelAndView getRadiologyReportTemplatesTab(HttpServletRequest request) {
        
        final ModelAndView modelAndView = new ModelAndView(RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW);
        request.getSession()
                .setAttribute(RadiologyWebConstants.RADIOLOGY_DASHBOARD_TAB_SESSION_ATTRIBUTE,
                    RADIOLOGY_REPORT_TEMPLATES_TAB_REQUEST_MAPPING);
        return modelAndView;
    }
    
    /**
     * Handle request for importing new {@code MrrtReportTemplate}.
     * 
     * @param request the HttpServletRequest to import MrrtReportTemplates
     * @param templateFile the MrrtReportTemplate file to be imported
     * @return model and view of the radiology dashboard report templates page with success or failure message in session
     *         attribute
     * @throws IOException when templateFile could not be read or is invalid
     * @should give error message when template file is empty
     * @should set error message in session when api exception is thrown
     * @should set error message in session when io exception is thrown
     * @should give success message when import was successful
     */
    @RequestMapping(method = RequestMethod.POST, params = "uploadReportTemplate")
    protected ModelAndView uploadReportTemplate(HttpServletRequest request, @RequestParam MultipartFile templateFile)
            throws IOException {
        
        if (templateFile.isEmpty()) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "radiology.MrrtReportTemplate.not.imported.empty");
            return new ModelAndView(RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW);
        }
        
        try {
            mrrtReportTemplateService.importMrrtReportTemplate(templateFile.getInputStream());
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_MSG_ATTR, "radiology.MrrtReportTemplate.imported");
        }
        catch (IOException exception) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                        "Failed to import " + templateFile.getOriginalFilename() + " => " + exception.getMessage());
        }
        catch (APIException exception) {
            request.getSession()
                    .setAttribute(WebConstants.OPENMRS_ERROR_ATTR,
                        "Failed to import " + templateFile.getOriginalFilename() + " => " + exception.getMessage());
        }
        return new ModelAndView(RADIOLOGY_REPORT_TEMPLATES_TAB_VIEW);
    }
}
