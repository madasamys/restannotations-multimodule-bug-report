package org.sample.restapi.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wicketstuff.rest.annotations.MethodMapping;
import org.wicketstuff.rest.annotations.ResourcePath;
import org.wicketstuff.rest.annotations.parameters.HeaderParam;
import org.wicketstuff.rest.contenthandling.json.objserialdeserial.GsonObjectSerialDeserial;
import org.wicketstuff.rest.contenthandling.json.webserialdeserial.JsonWebSerialDeserial;
import org.wicketstuff.rest.resource.AbstractRestResource;
import org.wicketstuff.rest.utils.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Madasamy
 * @since x.x
 */
@ResourcePath("/api")
public class LoginRestResoucre extends AbstractRestResource<JsonWebSerialDeserial>
{
    public static Logger logger = LoggerFactory.getLogger(LoginRestResoucre.class);

    public LoginRestResoucre()
    {
        super(new JsonWebSerialDeserial(new GsonObjectSerialDeserial()));
    }

    @MethodMapping(value = "/login", httpMethod = HttpMethod.GET)
    public Authentication login()
    {
        return new Authentication(CommonConstants.ACCESS_TOKEN);
    }
    

    public class Authentication
    {
        private String accessToken;

        public Authentication()
        {

        }

        public Authentication(String accessToken)
        {
            this.accessToken = accessToken;
        }

        public String getAccessToken()
        {
            return accessToken;
        }

        public void setAccessToken(String accessToken)
        {
            this.accessToken = accessToken;
        }
    }

    public class Connectivity
    {
        private int critical;
        private int fatal;

        public Connectivity(int critical, int fatal)
        {
            this.critical = critical;
            this.fatal = fatal;
        }

        public int getCritical()
        {
            return critical;
        }

        public void setCritical(int critical)
        {
            this.critical = critical;
        }

        public int getFatal()
        {
            return fatal;
        }

        public void setFatal(int fatal)
        {
            this.fatal = fatal;
        }
    }
}
