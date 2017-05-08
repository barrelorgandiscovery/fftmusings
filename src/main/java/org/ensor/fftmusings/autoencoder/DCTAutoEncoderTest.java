/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.fftmusings.autoencoder;

import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.ensor.fftmusings.audio.AudioSample;
import org.ensor.fftmusings.audio.DCT;
import org.ensor.fftmusings.audio.FFTOverlap;
import org.ensor.fftmusings.audio.FFTToPNG;
import org.ensor.fftmusings.audio.WAVFileWriter;
import org.ensor.fftmusings.pipeline.ChannelDuplicator;
import org.ensor.fftmusings.pipeline.Pipeline;

/**
 *
 * @author jona
 */
public class DCTAutoEncoderTest {

    public static void main(String[] args) throws IOException {
        MultiLayerNetwork model = ModelSerializer.restoreMultiLayerNetwork("data/daa/model-39.faa");
        
        //model.init();
        
        String inputFilename = "data/fft/0.fft";
        String outputFilename = "sample.wav";
        
        // Read DCT data from file and write a corresponding
        // .wav file based on that after passing it through the
        // auto-encoder to see what the network has learned.
        try (FFTOverlap.Reader wavFileIterator = FFTOverlap.createReader(inputFilename)) {
            new Pipeline(new AutoEncoderProcessor(model))
                .add(new FFTToPNG("fft-magnitude-daa2.png", false))
                .add(new FFTOverlap.Reverse(1024))
                .add(new ChannelDuplicator(AudioSample.class, 2))
                .add(WAVFileWriter.create(outputFilename))
                .execute(wavFileIterator);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not process file " + inputFilename, ex);
        }
    }
}
