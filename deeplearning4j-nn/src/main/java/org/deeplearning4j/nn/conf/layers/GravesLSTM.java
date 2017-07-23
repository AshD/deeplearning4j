/*-
 *
 *  * Copyright 2015 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package org.deeplearning4j.nn.conf.layers;

import lombok.*;
import org.deeplearning4j.nn.api.Layer;
import org.deeplearning4j.nn.api.ParamInitializer;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.memory.LayerMemoryReport;
import org.deeplearning4j.nn.layers.recurrent.LSTMHelpers;
import org.deeplearning4j.nn.params.GravesLSTMParamInitializer;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.util.LayerValidation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.activations.impl.ActivationSigmoid;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.Collection;
import java.util.Map;

/**
 * LSTM recurrent net, based on Graves: Supervised Sequence Labelling with Recurrent Neural Networks
 * http://www.cs.toronto.edu/~graves/phd.pdf
 *
 * @author Alex Black
 * @see LSTM LSTM class, for the version without peephole connections
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GravesLSTM extends AbstractLSTM {

    private double forgetGateBiasInit;
    private IActivation gateActivationFn = new ActivationSigmoid();

    private GravesLSTM(Builder builder) {
        super(builder);
        this.forgetGateBiasInit = builder.forgetGateBiasInit;
        this.gateActivationFn = builder.gateActivationFn;
    }

    @Override
    public Layer instantiate(NeuralNetConfiguration conf, Collection<IterationListener> iterationListeners,
                    int layerIndex, INDArray layerParamsView, boolean initializeParams) {
        LayerValidation.assertNInNOutSet("GravesLSTM", getLayerName(), layerIndex, getNIn(), getNOut());
        org.deeplearning4j.nn.layers.recurrent.GravesLSTM ret =
                        new org.deeplearning4j.nn.layers.recurrent.GravesLSTM(conf);
        ret.setListeners(iterationListeners);
        ret.setIndex(layerIndex);
        ret.setParamsViewArray(layerParamsView);
        Map<String, INDArray> paramTable = initializer().init(conf, layerParamsView, initializeParams);
        ret.setParamTable(paramTable);
        ret.setConf(conf);
        return ret;
    }

    @Override
    public ParamInitializer initializer() {
        return GravesLSTMParamInitializer.getInstance();
    }

    @Override
    public LayerMemoryReport getMemoryReport(InputType inputType) {
        //TODO - CuDNN etc
        return LSTMHelpers.getMemoryReport(this, inputType);
    }

    @AllArgsConstructor
    public static class Builder extends AbstractLSTM.Builder<Builder> {

        @SuppressWarnings("unchecked")
        public GravesLSTM build() {
            return new GravesLSTM(this);
        }
    }

}
