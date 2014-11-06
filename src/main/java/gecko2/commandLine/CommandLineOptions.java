package gecko2.commandLine;

import gecko2.datastructures.Parameter;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sascha Winter (sascha.winter@uni-jena.de)
 */
public class CommandLineOptions {
    private static final Logger logger = LoggerFactory.getLogger(CommandLineOptions.class);

    /*
     * Algorithm parameters
     */
    @Option(name="-d", aliases = "--distance", usage = "The maximum allowed distance.")
    private int maxDistance = -1;

    @Option(name="-dT", aliases = "--distanceTable", usage = "A string of arrays of maximum allowed distances.\n" +
            "Each array has to have 4 elements, the maximum number of additions, the maximum number of losses," +
            "the maximum sum of deletions and losses and the minimum size the parameters apply to.\n" +
            "e.g. \"[1, 0, 1, 3],[2, 1, 2, 4]\" allows 1 loss for size 3 and 2 losses and/or 1 deletions for size 4.\n" +
            "Has to be a single string, so either contained in \"\" or not containing any blanks.",
            handler = DistanceTableOptionHandler.class)
    private int[][] distanceTable;

    @Option(name="-s", aliases = "--size", required = true, usage = "The minimum cluster size.")
    private int minClusterSize;

    @Option(name="-q", aliases = "--quorum", usage = "The minimum number of covered genomes.")
    private int minCoveredGenomes = 0;

    @Option(name="-gGF", aliases = "--genomeGroupingFactor", usage = "All genomes with lower breakpoint distance are treated as one group.")
    private double genomeGroupingFactor = 1.1;

    @Option(name="-o", aliases = "--operationMode", usage = "The operation mode, [reference] cluster (default), [median] gene cluster, or [center] gene cluster.")
    private Parameter.OperationMode operationMode = Parameter.OperationMode.reference;

    @Option(name="-r", aliases = "--referenceGenomeName", usage = "Name of the reference genome.\n" +
            "If not set all genomes are used as reference.\n" +
            "The name has to be uniquely contained in a single genome.")
    private String referenceGenomeName = "";

    /*
     * Files
     */
    @Option(name="-in", aliases = "--Infile", usage = "The .gck or .cog input file.")
    private File infile = null;


    @Option(name="-gL", aliases = "--genomeList", handler=GenomeListOptionHandler.class, usage = "The indices of the genomes that shall be imported from the .cog file.\n" +
            "A String containing a comma separated list of integers (\"1, 3, 5, 8\").\n" +
            "Has to be a single string, so either contained in \"\" or not containing any blanks.")
    private List<Integer> genomeList = null;

    @Option(name="-out", aliases = "--Outfile", required = true, usage = "The output file.")
    private File outfile = null;

    /*
     * Others
     */
    @Option(name="-gui", usage = "Start the gui.", help=true)
    private boolean gui = false;

    @Option(name="-h", aliases = "--help", usage = "Show this help and exit.", help= true)
    private boolean help = false;

    /*
     * Getters
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    public int[][] getDistanceTable() {
        return distanceTable;
    }

    public int getMinClusterSize() {
        return minClusterSize;
    }

    public int getMinCoveredGenomes() {
        return minCoveredGenomes;
    }

    public double getGenomeGroupingFactor() {
        return genomeGroupingFactor;
    }

    public Parameter.OperationMode getOperationMode() {
        return operationMode;
    }

    public String getReferenceGenomeName() {
        return referenceGenomeName;
    }

    public File getInfile() {
        return infile;
    }

    public List<Integer> getGenomeList() {
        return genomeList;
    }

    public File getOutfile() {
        return outfile;
    }

    public boolean useGui() {
        return gui;
    }

    public boolean showHelp() {
        return help;
    }

    /**
     * Validates all the parameters
     * @return
     */
    public void validate(CmdLineParser parser) throws CmdLineException {
        if (gui)
            return;
        if ((distanceTable == null || distanceTable.length == 0) && (maxDistance < 0))
            throw new CmdLineException(parser, "Not running gui and missing either \"-d\" or \"-dT\" or distance < 0.");
        if ((distanceTable != null && distanceTable.length > 0) && (maxDistance >= 0))
            throw new CmdLineException(parser, "Not running gui and both \"-d\" and \"-dT\" set.");
    }

    public static class DistanceTableOptionHandler extends OptionHandler<int[][]> {
        public DistanceTableOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super int[][]> setter) {
            super(parser, option, setter);
            if (setter.asFieldSetter()==null)
                throw new IllegalArgumentException("MapOptionHandler can only work with fields");
        }

        @Override
        public int parseArguments(Parameters params) throws CmdLineException {
            String full = params.getParameter(0).trim();
            String[] delimitedStrings = full.split("\\]");
            Map<Integer, int[]> mapping = new HashMap<>();
            int maxSize = 0;
            for (String delimitedString : delimitedStrings) {
                String cleanedString = delimitedString.substring(delimitedString.indexOf("[") + 1);
                String[] singleValues = cleanedString.split(",");

                if (singleValues.length != 4) {
                    CmdLineException e = new CmdLineException(owner, Messages.ILLEGAL_OPERAND, params.getParameter(-1), full);
                    logger.warn("Malformed parameters at {}", singleValues, e);
                    throw e;
                } try {
                    int size = Integer.parseInt(singleValues[3].trim());
                    if (size < 0 || mapping.containsKey(size)){
                        CmdLineException e = new CmdLineException(owner, Messages.ILLEGAL_OPERAND, params.getParameter(-1), full);
                        logger.warn("Size < 0 or duplicate size {}", size, e);
                        throw e;
                    }
                    if (size > maxSize)
                        maxSize = size;
                    int[] d = new int[3];
                    d[0] = Integer.parseInt(singleValues[0].trim());
                    d[1] = Integer.parseInt(singleValues[1].trim());
                    d[2] = Integer.parseInt(singleValues[2].trim());
                    mapping.put(size, d);
                } catch (NumberFormatException e){
                    CmdLineException ex = new CmdLineException(owner, String.format("%s is ont a valid value for %s", full, params.getParameter(-1)), e);
                    logger.warn("Not a number in {}", singleValues, ex);
                    throw ex;
                }
            }
            int[][] table = new int[maxSize+1][];
            int[] lastValues = new int[]{0, 0, 0};
            for (int i=0; i<=maxSize; i++){
                int[] values = mapping.get(i);
                if (values == null)
                    table[i] = Arrays.copyOf(lastValues, lastValues.length);
                else {
                    table[i] = values;
                    lastValues = values;
                }
            }
            setter.asFieldSetter().addValue(table);
            return 1;
        }

        @Override
        public String getDefaultMetaVariable() {
            return "<[N,N,N,N],...>";
        }
    }


    public static class GenomeListOptionHandler extends DelimitedOptionHandler<Integer> {
        public GenomeListOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Integer> setter) {
            super(parser, option, setter, ",", new IntegerTrimmingOptionHandler(parser, option, setter));
        }
    }

    public static class IntegerTrimmingOptionHandler extends OneArgumentOptionHandler<Integer> {
        public IntegerTrimmingOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Integer> setter) {
            super(parser, option, setter);
        }

        @Override
        protected Integer parse(String argument) throws CmdLineException {
            try {
                return Integer.parseInt(argument.trim());
            } catch (NumberFormatException e) {
                throw new CmdLineException(owner, e);
            }
        }
    }



}
