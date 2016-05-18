package com.xtaudio.xt;

import com.sun.jna.Pointer;

public class RenderAdvanced {

    static double phase = 0.0;
    static final double FREQUENCY = 660.0;

    static void readLine() {
        System.out.println("Press any key to continue...");
        System.console().readLine();
    }

    static float nextSine(double sampleRate) {
        phase += FREQUENCY / sampleRate;
        if (phase >= 1.0)
            phase = -1.0;
        return (float) Math.sin(phase * Math.PI);
    }

    static void renderInterleaved(XtStream stream, Object input, Object output, int frames,
            double time, long position, boolean timeValid, long error, Object user) throws Exception {

        XtFormat format = stream.getFormat();
        for (int f = 0; f < frames; f++) {
            float sine = nextSine(format.mix.rate);
            for (int c = 0; c < format.outputs; c++)
                ((float[]) output)[f * format.outputs + c] = sine;
        }
    }

    static void renderInterleavedRaw(XtStream stream, Object input, Object output, int frames,
            double time, long position, boolean timeValid, long error, Object user) throws Exception {

        XtFormat format = stream.getFormat();
        int sampleSize = XtAudio.getSampleAttributes(format.mix.sample).size;
        for (int f = 0; f < frames; f++) {
            float sine = nextSine(format.mix.rate);
            for (int c = 0; c < format.outputs; c++)
                ((Pointer) output).setFloat((f * format.outputs + c) * sampleSize, sine);
        }
    }

    static void renderNonInterleaved(XtStream stream, Object input, Object output, int frames,
            double time, long position, boolean timeValid, long error, Object user) throws Exception {

        XtFormat format = stream.getFormat();
        for (int f = 0; f < frames; f++) {
            float sine = nextSine(format.mix.rate);
            for (int c = 0; c < format.outputs; c++)
                ((float[][]) output)[c][f] = sine;
        }
    }

    static void renderNonInterleavedRaw(XtStream stream, Object input, Object output, int frames,
            double time, long position, boolean timeValid, long error, Object user) throws Exception {

        XtFormat format = stream.getFormat();
        int sampleSize = XtAudio.getSampleAttributes(format.mix.sample).size;
        for (int f = 0; f < frames; f++) {
            float sine = nextSine(format.mix.rate);
            for (int c = 0; c < format.outputs; c++)
                ((Pointer) output).getPointer(c * Pointer.SIZE).setFloat(f * sampleSize, sine);
        }
    }

    public static void main(String[] args) throws Exception {

        try (XtAudio audio = new XtAudio(null, null, null, null)) {

            XtService service = XtAudio.getServiceBySetup(XtSetup.CONSUMER_AUDIO);
            XtFormat format = new XtFormat(new XtMix(44100, XtSample.FLOAT32), 0, 0, 2, 0);
            try (XtDevice device = service.openDefaultDevice(true)) {

                if (device == null) {
                    System.out.println("No default device found.");
                    return;
                }

                if (!device.supportsFormat(format)) {
                    System.out.println("Format not supported.");
                    return;
                }

                XtBuffer buffer = device.getBuffer(format);

                try (XtStream stream = device.openStream(format, true, false, buffer.current,
                        RenderAdvanced::renderInterleaved, null)) {
                    stream.start();
                    System.out.println("Rendering interleaved...");
                    readLine();
                    stream.stop();
                }

                try (XtStream stream = device.openStream(format, true, true, buffer.current,
                        RenderAdvanced::renderInterleavedRaw, null)) {
                    stream.start();
                    System.out.println("Rendering interleaved, raw buffers...");
                    readLine();
                    stream.stop();
                }

                try (XtStream stream = device.openStream(format, false, false, buffer.current,
                        RenderAdvanced::renderNonInterleaved, null)) {
                    stream.start();
                    System.out.println("Rendering non-interleaved...");
                    readLine();
                    stream.stop();
                }

                try (XtStream stream = device.openStream(format, false, true, buffer.current,
                        RenderAdvanced::renderNonInterleavedRaw, null)) {
                    stream.start();
                    System.out.println("Rendering non-interleaved, raw buffers...");
                    readLine();
                    stream.stop();
                }

                XtFormat sendToLeft = new XtFormat(new XtMix(44100, XtSample.FLOAT32), 0, 0, 1, 1L << 0);
                try (XtStream stream = device.openStream(sendToLeft, true, false, buffer.current,
                        RenderAdvanced::renderInterleaved, null)) {
                    stream.start();
                    System.out.println("Rendering channel mask, left channel...");
                    readLine();
                    stream.stop();
                }

                XtFormat sendToRight = new XtFormat(new XtMix(44100, XtSample.FLOAT32), 0, 0, 1, 1L << 1);
                try (XtStream stream = device.openStream(sendToRight, true, false, buffer.current,
                        RenderAdvanced::renderInterleaved, null)) {
                    stream.start();
                    System.out.println("Rendering channel mask, right channel...");
                    readLine();
                    stream.stop();
                }
            }
        }
    }
}