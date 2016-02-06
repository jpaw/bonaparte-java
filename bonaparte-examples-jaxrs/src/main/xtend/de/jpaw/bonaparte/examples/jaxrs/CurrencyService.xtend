package de.jpaw.bonaparte.examples.jaxrs

import de.jpaw.bonaparte.core.BonaCustom
import de.jpaw.bonaparte.core.ByteArrayComposer
import de.jpaw.bonaparte.core.CSVComposer
import de.jpaw.bonaparte.core.CSVConfiguration
import de.jpaw.bonaparte.poi.ExcelComposer
import de.jpaw.bonaparte.poi.ExcelXComposer
import de.jpaw.bonaparte.pojos.examples.jaxrs.BonCurrency
import de.jpaw.bonaparte.xml.XmlComposer
import de.jpaw.bonaparte.xml.XmlListWrapper
import de.jpaw.bonaparte.xml.XmlUtil
import java.util.Currency
import java.util.List
import javax.ejb.Stateless
import javax.ws.rs.ApplicationPath
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Application
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.xml.bind.JAXBContext
import de.jpaw.bonaparte.core.JsonComposer
import de.jpaw.bonaparte.core.MimeTypes

/**
 * REST Service to provide currency data is various formats.
 * 
 * access via http://localhost:8080/bonaparte-examples-jaxrs-3.5.0-SNAPSHOT/jaxrs/format/xls, for example
 *
 */
@Stateless
@ApplicationPath("/jaxrs")
@Path("currency")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
class CurrencyService extends Application {
    private static final String MIME_TYPE_BON = MimeTypes.MIME_TYPE_BONAPARTE
    private static final String MIME_TYPE_XLS = "application/vnd.ms-excel"
    private static final String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    private static final JAXBContext context = XmlUtil.getJaxbContext("de.jpaw.bonaparte.pojos.examples.jaxrs")
        
    def static private toBon(Currency it) {
        return new BonCurrency(currencyCode, defaultFractionDigits, displayName, numericCode, symbol)
    }
    
    def static private Response inFormat(List<? extends BonaCustom> objs, String format) {
        switch (format) {
            case 'xls': {
                val ec = new ExcelComposer
                ec.writeTransmission(objs)
                Response.ok(ec.bytes, MIME_TYPE_XLS).build
            }
            case 'xlsx': {
                val ec = new ExcelXComposer
                ec.writeTransmission(objs)
                Response.ok(ec.bytes, MIME_TYPE_XLSX).build
            }
            case 'xml': {
                val ec = new XmlComposer(context, true, false)
                Response.ok(ec.marshal(new XmlListWrapper(objs)), MediaType.TEXT_XML).build
            }
            case 'bon': {
                val bac = new ByteArrayComposer();
                bac.writeTransmission(objs)
                Response.ok(bac.bytes, MIME_TYPE_BON).build
            }
            case 'json': {
                Response.ok(JsonComposer.toJsonString(objs), MediaType.APPLICATION_JSON).build
            }
            case 'csv': {
                val cfg = new CSVConfiguration.Builder => [
                    usingSeparator(" | ")
                    booleanTokens("TRUE", "FALSE")
                ]
                val sb = new StringBuilder
                val ec = new CSVComposer(sb, cfg.build)
                for (curr : objs)
                    ec.writeRecord(curr)
                Response.ok(sb.toString, "text/csv").build
            }
            default:
                Response.status(Response.Status.BAD_REQUEST).build
        }
    }
    
    // explicit format as a parameter
    @GET
    @Path("format/{format}")
    def public allInFormat(@PathParam("format") String format) {
        return Currency.availableCurrencies.map[toBon].toList.inFormat(format)
    }
    
    @GET
    @Path("format/{format}/{code}")
    def public inFormat(@PathParam("format") String format, @PathParam("code") String code) {
        val currency = Currency.getInstance(code)
        if (currency === null)
            throw new WebApplicationException("Unknown currency code " + code, Response.Status.BAD_REQUEST);
        return #[ currency.toBon ].inFormat(format)
    }

    // traditional JAX RS formats (single type)
    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON)
    def public allAsJson() {
        return Currency.availableCurrencies.map[toBon]
    }

    @GET
    @Path("json/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    def public asJson(@PathParam("code") String code) {
        val currency = Currency.getInstance(code)
        if (currency === null)
            throw new WebApplicationException("Unknown currency code " + code, Response.Status.BAD_REQUEST);
        return currency.toBon
    }

    @GET
    @Path("xml")
    @Produces(MediaType.TEXT_XML)
    def public allAsXml() {
        return Currency.availableCurrencies.map[toBon].toList
    }

    @GET
    @Path("xml/{code}")
    @Produces(MediaType.TEXT_XML)
    def public asXml(@PathParam("code") String code) {
        val currency = Currency.getInstance(code)
        if (currency === null)
            throw new WebApplicationException("Unknown currency code " + code, Response.Status.BAD_REQUEST);
        return currency.toBon
    }
    
    // showcase for the jaxrs converter
    @GET
    @Path("bon")
    @Produces(MIME_TYPE_BON)
    def public allAsBonaPortable() {
        return Currency.availableCurrencies.map[toBon]
    }

    @GET
    @Path("bon/{code}")
    @Produces(MIME_TYPE_BON)
    def public asBonaPortable(@PathParam("code") String code) {
        val currency = Currency.getInstance(code)
        if (currency === null)
            throw new WebApplicationException("Unknown currency code " + code, Response.Status.BAD_REQUEST);
        return currency.toBon
    }

}
