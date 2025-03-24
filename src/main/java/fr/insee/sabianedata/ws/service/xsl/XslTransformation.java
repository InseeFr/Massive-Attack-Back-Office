package fr.insee.sabianedata.ws.service.xsl;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/**
 * Main Saxon Service used to perform XSLT transformations
 *
 * @author gerose
 */
@Slf4j
public class XslTransformation {

    private final TransformerFactory tFactory = new net.sf.saxon.TransformerFactoryImpl();

    public XslTransformation() {
        tFactory.setURIResolver(new ClasspathResourceURIResolver());
    }

    private void xslTransform(Transformer transformer, InputStream xmlInput, OutputStream xmlOutput) throws Exception {
        try {
            transformer.transform(new StreamSource(xmlInput), new StreamResult(xmlOutput));
        } catch (Exception e) {
            log.error("Error during XSL transformation", e);
            throw new Exception("Transformation failed", e);
        }
    }

    public void transformFods2XML(InputStream inputFile, OutputStream outputFile, InputStream xslSheet) throws Exception {
        Transformer transformer = tFactory.newTransformer(new StreamSource(xslSheet));
        xslTransform(transformer, inputFile, outputFile);
    }

    static class ClasspathResourceURIResolver implements URIResolver {
        @Override
        public Source resolve(String href, String base) throws TransformerException {
            log.debug("Resolving URI: href={}, base={}", href, base);

            // Adjust relative paths
            String resolvedPath = "/xslt/".concat(href);
            InputStream resourceStream = ClasspathResourceURIResolver.class.getResourceAsStream(resolvedPath);

            if (resourceStream == null) {
                log.error("Resource not found at: {}", resolvedPath);
                throw new TransformerException("Resource not found: " + resolvedPath);
            }

            log.debug("Successfully resolved XSL resource at: {}", resolvedPath);
            return new StreamSource(resourceStream);
        }
    }
}
