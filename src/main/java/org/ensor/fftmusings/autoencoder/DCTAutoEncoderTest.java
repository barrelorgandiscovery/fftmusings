/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.ensor.fftmusings.autoencoder;

import java.io.IOException;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.ensor.fftmusings.audio.AudioSample;
import org.ensor.fftmusings.audio.DCT;
import org.ensor.fftmusings.audio.WAVFileWriter;
import org.ensor.fftmusings.pipeline.ChannelDuplicator;
import org.ensor.fftmusings.pipeline.Pipeline;

/**
 *
 * @author jona
 */
public class DCTAutoEncoderTest {

    public static void main(String[] args) throws IOException {
        
        Autoencoder autoencoder = GenericAutoencoder.readAutoencoder("data/autoencoders/dct-512-400-mb100-lr1-sqrtscale/autoencoder.json");
        
        MultiLayerNetwork model = autoencoder.getModel();
        
        //model.init();
        
        String inputFilename = "data/dct/20.dct";
        String outputFilename = "sample.wav";
        
        // Read DCT data from file and write a corresponding
        // .wav file based on that after passing it through the
        // auto-encoder to see what the network has learned.
        try (DCT.Reader wavFileIterator = DCT.createReader(inputFilename)) {
            new Pipeline(new Layer.DCTToINDArray())
                .add(new Layer.ModelProcessor(model, 0, 1))
                .add(new Layer.ToPNG("middle.png"))
                .add(new Layer.ModelProcessor(model, 2, 2))
                .add(new Layer.INDArrayToDCT())
                .add(new DCT.Reverse(true))
                .add(new ChannelDuplicator(AudioSample.class, 2))
                .add(WAVFileWriter.create(outputFilename))
                .execute(wavFileIterator);
        }
        catch (Exception ex) {
            throw new RuntimeException("Could not process file " + inputFilename, ex);
        }
    }
}
