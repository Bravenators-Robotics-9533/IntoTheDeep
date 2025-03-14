package com.bravenatorsrobotics.utils;

import android.util.Pair;
import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlockDetectVisionPipeline {

    private ColorBlobLocatorProcessor colorLocator;

    public void initialize(HardwareMap hardwareMap) {

        this.colorLocator = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.RED)         // use a predefined color match
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)    // exclude blobs inside blobs
                .setRoi(ImageRegion.asUnityCenterCoordinates(-1, 1, 1, -1))  // search central 1/4 of camera view
                .setDrawContours(true)                        // Show contours on the Stream Preview
                .setBlurSize(5)                               // Smooth the transitions between different colors in image
                .build();

        VisionPortal portal = new VisionPortal.Builder()
                .addProcessor(colorLocator)
                .setCameraResolution(new Size(1920, 1080))
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setStreamFormat(VisionPortal.StreamFormat.MJPEG)
                .build();

        FtcDashboard.getInstance().startCameraStream(portal, 0);

    }

    public DetectionData detectBlock() {

        // Read the current list
        List<ColorBlobLocatorProcessor.Blob> blobs = this.colorLocator.getBlobs();

        // Filter out very small blocks
        ColorBlobLocatorProcessor.Util.filterByArea(10000, Double.MAX_VALUE, blobs);

        if(blobs.isEmpty())
            return null;

        // Get largest blob
        double largestBlobSize = 0;
        int largestBlobIndex = 0;

        for(int i = 0; i < blobs.size(); i++) {

            if(blobs.get(i).getContourArea() > largestBlobSize) {
                largestBlobSize = blobs.get(i).getContourArea();
                largestBlobIndex = i;
            }

        }

        ColorBlobLocatorProcessor.Blob blob = blobs.get(largestBlobIndex);

        // Derive all values and return
        return new DetectionData(
                blob.getBoxFit().boundingRect().x,
                blob.getBoxFit().boundingRect().x + blob.getBoxFit().boundingRect().width,
                blob.getBoxFit().boundingRect().y,
                blob.getBoxFit().boundingRect().width,
                blob.getContourPoints()
        );

    }

    public static class DetectionData {

        public final double x0, x1, y0;

        public final double w;

        public final double c;

        public final double angle;

        public DetectionData(double x0, double x1, double y0, double w, Point[] points) {

            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;

            this.w = w;

            this.c = (x0 + x1) / 2.0;

            this.angle = w > 1000 ? 90 : 0;

        }

        public void telemetry(Telemetry telemetry) {

            telemetry.addLine("Detection Data");
            telemetry.addLine("------------------------------");
            telemetry.addData("x0", x0);
            telemetry.addData("x1", x1);
            telemetry.addData("y0", y0);
            telemetry.addData("w", w);
            telemetry.addLine("------------------------------");
            telemetry.addData("c", c);
            telemetry.addData("angle", angle);

        }

    }
}
