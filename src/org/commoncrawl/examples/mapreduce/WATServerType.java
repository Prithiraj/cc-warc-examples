package org.commoncrawl.examples.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.commoncrawl.warc.WARCFileInputFormat;

/**
 * Server count example using the response metadata (WAT) from the Common Crawl dataset.
 *
 * @author Stephen Merity (Smerity)
 */
public class WATServerType extends Configured implements Tool {
	private static final Logger LOG = Logger.getLogger(WATServerType.class);
	
	/**
	 * Main entry point that uses the {@link ToolRunner} class to run the Hadoop job. 
	 */
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new WATServerType(), args);
		System.exit(res);
	}

	/**
	 * Builds and runs the Hadoop job.
	 * @return	0 if the Hadoop job completes successfully and 1 otherwise.
	 */
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = getConf();
		conf.set("fs.s3n.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem");
		conf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem");
		conf.set("fs.s3.awsAccessKeyId",args[0]);
		conf.set("fs.s3.awsSecretAccessKey",args[1]);
		//
		Job job = new Job(conf);
		job.setJarByClass(WATServerType.class);
		job.setNumReduceTasks(1);
		
		//String inputPath = "data/*.warc.wat.gz";
		String inputPath = "s3://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2013-20/segments/1368696381249/wat/CC-MAIN-20130516092621-00000-ip-10-60-113-184.ec2.internal.warc.wat.gz";
		//inputPath = "s3n://aws-publicdatasets/common-crawl/crawl-data/CC-MAIN-2013-48/segments/1386163035819/wet/*.warc.wet.gz";
		LOG.info("Input path: " + inputPath);
		FileInputFormat.addInputPath(job, new Path(inputPath));
		
		String outputPath = "/tmp/cc/";
		FileSystem fs = FileSystem.newInstance(conf);
		if (fs.exists(new Path(outputPath))) {
			fs.delete(new Path(outputPath), true);
		}
		FileOutputFormat.setOutputPath(job, new Path(outputPath));
		
		job.setInputFormatClass(WARCFileInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(LongWritable.class);
	    
	    job.setMapperClass(ServerTypeMap.ServerMapper.class);
	    job.setReducerClass(LongSumReducer.class);
		
	    if (job.waitForCompletion(true)) {
	    	return 0;
	    } else {
	    	return 1;
	    }
	}
}
