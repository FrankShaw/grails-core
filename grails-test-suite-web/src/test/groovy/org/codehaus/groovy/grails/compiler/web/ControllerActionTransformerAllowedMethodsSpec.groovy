package org.codehaus.groovy.grails.compiler.web

import grails.artefact.Artefact
import grails.artefact.Enhanced
import grails.test.mixin.TestFor

import javax.servlet.http.HttpServletResponse

import spock.lang.Issue
import spock.lang.Specification


@TestFor(SomeAllowedMethodsController)
class ControllerActionTransformerAllowedMethodsSpec extends Specification {

    @Issue('GRAILS-8426')
    void 'Test @AllowedMethodsHandledAtCompileTime is added'() {
        when:
        def annotation = SomeAllowedMethodsController.getAnnotation(Enhanced)

        then:
        annotation
        
        and:
        'allowedMethods' in annotation.enhancedFor()
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is not specified in allowedMethods'() {
        when:
        controller.anyMethodAllowed()
        
        then:
        response.status == HttpServletResponse.SC_OK
        response.contentAsString == 'Success'
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a single request method using the valid request method'() {
        when:
        request.method = 'POST'
        controller.onlyPostAllowed()
        
        then:
        response.status == HttpServletResponse.SC_OK
        response.contentAsString == 'Success'
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a single request method that is not specified in all upper case using the valid request method'() {
        when:
        request.method = 'POST'
        controller.mixedCasePost()
        
        then:
        response.status == HttpServletResponse.SC_OK
        response.contentAsString == 'Success'
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a single request method that is not specified in all upper case using an invalid request method'() {
        when:
        request.method = 'GET'
        controller.mixedCasePost()
        
        then:
        response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a List of request methods using an ivalid request method'() {
        when:
        request.method = 'GET'
        controller.postOrPutAllowed()
        
        then:
        response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a List of request methods using the first specified valid valid request method'() {
        when:
        request.method = 'POST'
        controller.postOrPutAllowed()
        
        then:
        response.status == HttpServletResponse.SC_OK
        response.contentAsString == 'Success'
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a List of request methods using the last specified valid valid request method'() {
        when:
        request.method = 'PUT'
        controller.postOrPutAllowed()
        
        then:
        response.status == HttpServletResponse.SC_OK
        response.contentAsString == 'Success'
    }
    
    @Issue('GRAILS-8426')
    void 'Test accessing an action that is limited to a single request method using an invalid request method'() {
        when:
        request.method = 'PUT'
        controller.onlyPostAllowed()
        
        then:
        response.status == HttpServletResponse.SC_METHOD_NOT_ALLOWED
    }
    
    @Issue('GRAILS-11444')
    void 'Test invoking a restricted action method from an unrestricted action method'() {
        when: 'an unrestricted action method invokes a restricted action method'
        controller.callPostMethod()
        
        then: 'the allowedMethods should not be checked by the restricted method'
        response.status == HttpServletResponse.SC_OK
    }
    
    @Issue('GRAILS-11444')
    void 'Test invoking a restricted action method from another restricted action method'() {
        when: 'a restricted action method invokes another restricted action method'
        request.method = 'PUT'
        controller.callPostMethodFromPutMethod()
            
        then: 'the allowedMethods should not be checked by the second method'
        response.status == HttpServletResponse.SC_OK
    }
}

@Artefact('Controller')
class SomeAllowedMethodsController {
    
    static allowedMethods = [callPostMethodFromPutMethod: 'PUT', 
                             onlyPostAllowed: 'POST', 
                             postOrPutAllowed: ['POST', 'PUT'], 
                             mixedCasePost: 'pOsT']
    
    def anyMethodAllowed() {
        render 'Success'
    }
    
    def onlyPostAllowed() {
        render 'Success'
    }
    
    def postOrPutAllowed() {
        render 'Success'
    }
    
    def mixedCasePost() {
        render 'Success'
    }
    
    def callPostMethod() {
        onlyPostAllowed()
    }
    
    def callPostMethodFromPutMethod() {
        onlyPostAllowed()
    }
}
