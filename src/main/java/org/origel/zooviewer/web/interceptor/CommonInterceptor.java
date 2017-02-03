package org.origel.zooviewer.web.interceptor;

import java.util.Locale;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import net.paoding.rose.web.ControllerInterceptorAdapter;
import net.paoding.rose.web.Invocation;

import org.apache.commons.lang.StringUtils;
import org.origel.zooviewer.util.VmUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContext;


@Component
public class CommonInterceptor extends ControllerInterceptorAdapter {
    static final Logger logger = LoggerFactory.getLogger(CommonInterceptor.class);

    private final static Properties properties = new Properties();
    static {
    	try {
			properties.load(CommonInterceptor.class.getResourceAsStream("/config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    };

    private final static VmUtils V = new VmUtils();
	@Override
	public Object before(Invocation inv) throws Exception {
		inv.addModel("contextPath", inv.getRequest().getContextPath());
		inv.addModel("now", System.currentTimeMillis());

		inv.addModel("P", properties);
		inv.addModel("V", V);
		
        RequestContext requestContext = new RequestContext(new RequestWrapper(inv.getRequest()));
        inv.addModel("R", requestContext);
		return true;
	}
	
    public static class RequestWrapper extends HttpServletRequestWrapper{

        @Override
        public Locale getLocale() {
            String localeStr = this.getParameter("locale");
            if(StringUtils.isNotBlank(localeStr)){
                try{
                    if(localeStr.contains("_")){
                        String field[] = localeStr.split("_");
                        Locale locale = new Locale(field[0], field[1]);
                        return locale;
                    }
                    return new Locale(localeStr);
                }catch(Exception e){
                	logger.error("", e);
                }
            }

            return super.getLocale();
        }

        public RequestWrapper(HttpServletRequest request) {
            super(request);
        }
    }
}
