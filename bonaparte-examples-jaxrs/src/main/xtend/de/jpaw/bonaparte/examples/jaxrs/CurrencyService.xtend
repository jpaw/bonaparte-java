package de.jpaw.bonaparte.examples.jaxrs

import de.jpaw.bonaparte.core.BonaCustom
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

/**
 * REST Service to provice currency data is various formats.
 * 
 * access via http://localhost:8080/bonaparte-examples-jaxrs-3.5.0-SNAPSHOT/jaxrs/currency/xls, for example
 *
 */
@Stateless
@ApplicationPath("/jaxrs")
@Path("currency")
//@Consumes(MediaType.APPLICATION_JSON)
//@Produces(MediaType.APPLICATION_JSON)
class CurrencyService extends Application {
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
                ec.writeList(objs)
                Response.ok(ec.bytes, MIME_TYPE_XLS).build
            }
            case 'xlsx': {
                val ec = new ExcelXComposer
                ec.writeList(objs)
                Response.ok(ec.bytes, MIME_TYPE_XLSX).build
            }
            case 'xml': {
                val ec = new XmlComposer(context, true, false)
                Response.ok(ec.marshal(new XmlListWrapper(objs)), "text/xml").build
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
    def public allAsXls() {
        return Currency.availableCurrencies.map[toBon]
    }

    @GET
    @Path("xml/{code}")
    @Produces(MediaType.TEXT_XML)
    def public asXls(@PathParam("code") String code) {
        val currency = Currency.getInstance(code)
        if (currency === null)
            throw new WebApplicationException("Unknown currency code " + code, Response.Status.BAD_REQUEST);
        return currency.toBon
    }
}
