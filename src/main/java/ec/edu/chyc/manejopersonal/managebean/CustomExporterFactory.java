/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.chyc.manejopersonal.managebean;

/*
 * Copyright 2011-2015 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import javax.faces.FacesException;
import org.primefaces.extensions.component.exporter.ExcelExporter;
import org.primefaces.extensions.component.exporter.Exporter;
import org.primefaces.extensions.component.exporter.ExporterFactory;
import org.primefaces.extensions.component.exporter.PDFExporter;

/**
 * <code>Exporter</code> component.
 *
 * @author Sudheer Jonna / last modified by $Author$
 * @since 0.7.0
 */
/*public class CustomExporterFactory {

    private static final String KEY = ExporterFactoryProvider.class.getName();

    public static ExporterFactory getExporterFactory(FacesContext context) {

        ExporterFactory factory = (ExporterFactory) context.getExternalContext().getApplicationMap().get(KEY);

        if (factory == null) {
            ServiceLoader<ExporterFactory> loader = ServiceLoader.load(ExporterFactory.class);
            for (ExporterFactory currentFactory : loader) {
                factory = currentFactory;
                break;
            }

            if (factory == null) {
                factory = new DefaultExporterFactory();
            }

            context.getExternalContext().getApplicationMap().put(KEY, factory);
        }

        return factory;
    }
}
*/
public class CustomExporterFactory implements ExporterFactory {

    static public enum ExporterType {
        PDF,
        XLSX
    }

    @Override
    public Exporter getExporterForType(String type) {

        Exporter exporter = null;

        try {
            ExporterType exporterType = ExporterType.valueOf(type.toUpperCase());

            switch (exporterType) {

                case PDF:
                    exporter = new PDFCustomExporter();
                    break;

                case XLSX:
                    exporter = new ExcelCustomExporter();
                    break;

                default: {
                    exporter = new PDFCustomExporter();
                    break;
                }

            }
        } catch (IllegalArgumentException e) {
            throw new FacesException(e);
        }

        return exporter;
    }

}