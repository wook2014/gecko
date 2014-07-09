package gecko2;

import gecko2.algorithm.GeneCluster;
import gecko2.algorithm.Genome;
import gecko2.algorithm.Parameter;
import gecko2.io.DataSetWriter;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Sascha Winter (sascha.winter@uni-jena.de)
 */
public class CommandLineExecution {
    public static void runAlgorithm(CommandLineOptions options) {
        Parameter.ReferenceType refType;
        if (options.getReferenceGenomeName().equals(""))
            refType = Parameter.ReferenceType.allAgainstAll;
        else {
            refType = Parameter.ReferenceType.genome;
            Genome[] genomes = GeckoInstance.getInstance().getGenomes();

            int index = -1;
            for (int i=0; i<genomes.length; i++) {
                if (genomes[i].getName().contains(options.getReferenceGenomeName()))
                    if (index != -1)
                         throw new IllegalArgumentException(String.format("Error! Reference genome name (%s) is contained in more than one genome name (%s and %s)!", options.getReferenceGenomeName(), genomes[index].getName(), genomes[i].getName()));
                    else
                        index = i;
            }
            if (index == -1)
                 throw new IllegalArgumentException(String.format("Error! Reference genome name (%s) is not contained in any genome name!", options.getReferenceGenomeName()));

            Genome first = genomes[0];
            genomes[0] = genomes[index];
            genomes[index] = first;
        }

        Parameter parameter = new Parameter(options.getMaxDistance(), options.getMinClusterSize(), options.getMinCoveredGenomes(), Parameter.QUORUM_NO_COST, options.getOperationMode(), refType);

        // compute the clusters
        SwingWorker<List<GeneCluster>, Void> worker = GeckoInstance.getInstance().performClusterDetection(parameter, false, options.getGenomeGroupingFactor());
        try{
            worker.get(); // Blocks until worker is done()
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Remove outfile
        if (options.getOutfile().exists())
            options.getOutfile().delete();

        // Save session
        DataSetWriter.saveDataSetToFile(GeckoInstance.getInstance().getData(), options.getOutfile());
    }
}
