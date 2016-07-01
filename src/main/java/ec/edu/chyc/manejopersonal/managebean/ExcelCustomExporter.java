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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.datalist.DataList;
import org.primefaces.component.subtable.SubTable;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlCommandLink;
import javax.faces.context.FacesContext;
import javax.faces.component.UIPanel;

import java.util.List;
import java.util.Map;
import javax.faces.component.html.HtmlPanelGroup;
import org.primefaces.extensions.component.exporter.ExcelExporter;

/**
 * <code>Exporter</code> component.
 *
 * @author Sudheer Jonna / last modified by $Author$
 * @since 0.7.0
 */
public class ExcelCustomExporter extends ExcelExporter {
    @Override
    protected void addColumnValue(Row row, UIComponent component, String type) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        
        String value = "";
        if (component != null && component instanceof UIPanel) {
            
            for (UIComponent childComponent : component.getChildren()) {
                if (childComponent.isRendered()) {
                    String valChild = exportValue(FacesContext.getCurrentInstance(), childComponent);
                    
                    if (valChild != null && !valChild.isEmpty()) {
                        valChild = valChild.replace("<br/>", " ");
                        value += valChild + "\n";
                    }
                }
            }
        } else {
            value = component == null ? "" : exportValue(FacesContext.getCurrentInstance(), component);
        }        
        
        cell.setCellValue(new XSSFRichTextString(value));
        if (type.equalsIgnoreCase("facet")) {
            addFacetAlignments(component,cell);
        } else {
            addColumnAlignments(component, cell);
        }

    }
    @Override
    protected void addColumnValue(Row row, List<UIComponent> components, String type) {
        int cellIndex = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
        Cell cell = row.createCell(cellIndex);
        StringBuilder builder = new StringBuilder();
        FacesContext context = FacesContext.getCurrentInstance();

        for (UIComponent component : components) {
            if (component.isRendered()) {
                if (component instanceof HtmlPanelGroup) {
                    for (UIComponent childComponent : component.getChildren()) {
                        if (component.isRendered()) {
                            String valChild = exportValue(context, childComponent);

                            if (valChild != null && !valChild.isEmpty()) {
                                valChild = valChild.replace("<br/>", " ");
                                builder.append(valChild).append("\n");
                            }
                        }
                    }
                } else {
                    String value = exportValue(context, component);

                    if (value != null) {
                        builder.append(value);
                    }
                }
            }
        }

        cell.setCellValue(new XSSFRichTextString(builder.toString()));

        if (type.equalsIgnoreCase("facet")) {
            for (UIComponent component : components) {
                addFacetAlignments(component, cell);
            }
        } else {
            for (UIComponent component : components) {
                addColumnAlignments(component, cell);
            }
        }

    }

}
