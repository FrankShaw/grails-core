/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.groovy.grails.web.servlet.view;

import java.util.Locale;

import javax.servlet.ServletConfig;

import org.codehaus.groovy.grails.web.sitemesh.GrailsLayoutView;
import org.codehaus.groovy.grails.web.sitemesh.GroovyPageLayoutFinder;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;

public class GrailsLayoutViewResolver implements LayoutViewResolver, Ordered, ServletConfigAware, ApplicationContextAware {
    ViewResolver innerViewResolver;
    GroovyPageLayoutFinder groovyPageLayoutFinder;
    int order = Ordered.LOWEST_PRECEDENCE - 30;
    
    public GrailsLayoutViewResolver(ViewResolver innerViewResolver, GroovyPageLayoutFinder groovyPageLayoutFinder) {
        this.innerViewResolver = innerViewResolver;
        this.groovyPageLayoutFinder = groovyPageLayoutFinder;
    }

    @Override
    public View resolveViewName(String viewName, Locale locale) throws Exception {
        View innerView = innerViewResolver.resolveViewName(viewName, locale);
        if(innerView instanceof SmartView && ((SmartView)innerView).isRedirectView()) { 
            return innerView;
        } else {
            return new GrailsLayoutView(groovyPageLayoutFinder, innerView);
        }
    }

    @Override
    public ViewResolver getInnerViewResolver() {
        return innerViewResolver;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void setServletConfig(ServletConfig servletConfig) {
        if(innerViewResolver instanceof ServletConfigAware) {
            ((ServletConfigAware)innerViewResolver).setServletConfig(servletConfig);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(innerViewResolver instanceof ApplicationContextAware) {
            ((ApplicationContextAware)innerViewResolver).setApplicationContext(applicationContext);
        }
    }
}
