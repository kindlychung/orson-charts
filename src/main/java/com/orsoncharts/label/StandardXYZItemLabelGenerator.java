/* ============
 * Orson Charts
 * ============
 * 
 * (C)opyright 2013, 2014, by Object Refinery Limited.
 * 
 * http://www.object-refinery.com/orsoncharts/index.html
 * 
 * Redistribution of this source file is prohibited.
 * 
 */

package com.orsoncharts.label;

import java.io.Serializable;
import java.util.Formatter;

import com.orsoncharts.data.xyz.XYZDataset;
import com.orsoncharts.data.xyz.XYZItemKey;
import com.orsoncharts.interaction.XYZDataItemSelection;
import com.orsoncharts.util.ArgChecks;

/**
 * A default implementation of the {@link XYZItemLabelGenerator} interface.  
 * The implementation uses a {@link java.util.Formatter} instance to generate
 * the item labels.  Four values are passed to the formatter for possible 
 * inclusion in the resulting label: (1) the key for the series, 
 * (2) the x-value (3) the y-value and (4) the z-value.
 * <br><br>
 * NOTE: This class is serializable, but the serialization format is subject 
 * to change in future releases and should not be relied upon for persisting 
 * instances of this class. 
 * 
 * @since 1.3
 */
@SuppressWarnings("serial")
public class StandardXYZItemLabelGenerator implements XYZItemLabelGenerator, 
        Serializable {

    /** 
     * A label template that will display the series key followed by the
     * (x, y, z) coordinates with 3 decimal places.
     */
    public static final String KEY_AND_COORDS_3DP_TEMPLATE 
            = "%s (%2$.3f, %3$.3f, %4$.3f)";
    
    /** 
     * A label template that will display the (x, y, z) coordinates with 3 
     * decimal places. 
     */
    public static final String COORDS_3DP_TEMPLATE = "(%2$.3f, %3$.3f, %4$.3f)";

    /** The default label template. */
    public static final String DEFAULT_TEMPLATE = KEY_AND_COORDS_3DP_TEMPLATE;
            
    /** The label template. */
    private String template;
    
    private XYZDataItemSelection itemSelection;
    
    /**
     * The default constructor.
     */
    public StandardXYZItemLabelGenerator() {
        this(DEFAULT_TEMPLATE);    
    }
    
    /**
     * Creates a new instance with the specified label template.
     * 
     * @param template  the label template (<code>null</code> not permitted). 
     */
    public StandardXYZItemLabelGenerator(String template) {
        ArgChecks.nullNotPermitted(template, "template");
        this.template = template;
        this.itemSelection = null;
    }

    /**
     * Returns the item selection (if this is non-<code>null</code>, labels 
     * will only be generated for the items in the selection).
     * 
     * @return The item selection (possibly <code>null</code>).
     */
    public XYZDataItemSelection getItemSelection() {
        return this.itemSelection;
    }
    
    /**
     * Sets the item selection, which can be used to specify a subset of the
     * data items that should have item labels generated.  If this is set to 
     * <code>null</code> then item labels will be generated for all items.
     * 
     * @param selection  the selection (<code>null</code> permitted). 
     */
    public void setItemSelection(XYZDataItemSelection selection) {
        this.itemSelection = selection;
    }
    
    /**
     * Generates a label for the specified data item.
     * 
     * @param dataset  the dataset (<code>null</code> not permitted).
     * @param seriesKey  the series key (<code>null</code> not permitted).
     * 
     * @return The series label (possibly <code>null</code>). 
     */
    @Override
    public String generateItemLabel(XYZDataset dataset, 
            Comparable<?> seriesKey, int itemIndex) {
        ArgChecks.nullNotPermitted(dataset, "dataset");
        ArgChecks.nullNotPermitted(seriesKey, "seriesKey");
        if (this.itemSelection != null) {
            XYZItemKey key = new XYZItemKey(seriesKey, itemIndex);
            if (!this.itemSelection.isSelected(key)) {
                return null;
            }
        }
        int seriesIndex = dataset.getSeriesIndex(seriesKey);
        Formatter formatter = new Formatter(new StringBuilder());
        double x = dataset.getX(seriesIndex, itemIndex);
        double y = dataset.getY(seriesIndex, itemIndex);
        double z = dataset.getZ(seriesIndex, itemIndex);
        formatter.format(this.template, seriesKey, x, y, z);
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    /**
     * Tests this label generator for equality with an arbitrary object.
     * 
     * @param obj  the object (<code>null</code> permitted).
     * 
     * @return A boolean. 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StandardXYZItemLabelGenerator)) {
            return false;
        }
        StandardXYZItemLabelGenerator that = (StandardXYZItemLabelGenerator) obj;
        if (!this.template.equals(that.template)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return this.template.hashCode();
    }

}
