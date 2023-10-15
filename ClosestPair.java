// Nama   : Rizal Dwi Anggoro
// NIM    : L0122142
// Github : https://github.com/rizalanggoro/DAA-ClosestPair-DevideAndConquer

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Random;

class Point {
  int x, y;

  Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return "(" + this.x + "," + this.y + ")";
  }
}

class Result {
  double distance;
  Point point1, point2;

  Result(double distance, Point point1, Point point2) {
    this.distance = distance;
    this.point1 = point1;
    this.point2 = point2;
  }
}

public class ClosestPair {
  public static void main(String[] args) {
    Logic logic = new Logic();
    DecimalFormat decimalFormat = new DecimalFormat("#,###");

    int pointsCount = 10;
    int maxPoint = 10;

    Point[] points = new Point[pointsCount];

    // generate random points
    logic.generateRandomPoints(points, pointsCount, maxPoint);

    // mengurutkan array dnc
    logic.sortPointsByX(points);

    printPoints(points);
    System.out.println("\nJarak terpendek:");

    // perhitungan menggunakan algoritma bruteforce dan devide and conquer
    // untuk membuktikan keakuratan antara kedua algoritma

    long bfStartTime = System.nanoTime();
    Result resultBf = logic.bruteForcePoints(points);
    long bfEndTime = System.nanoTime();
    long bfDuration = bfEndTime - bfStartTime;

    System.out.println("- Bruteforce");
    System.out.printf("  Titik   : %s - %s\n", resultBf.point1.toString(), resultBf.point2.toString());
    System.out.printf("  Jarak   : %g\n", resultBf.distance);
    System.out.printf("  Presisi : %g\n", logic.calculateDistance(resultBf.point1, resultBf.point2));
    System.out.printf("  Waktu   : %s ns\n", decimalFormat.format(bfDuration));

    long dncStartTime = System.nanoTime();
    Result resultDnC = logic.closestPair(points);
    long dncEndTime = System.nanoTime();
    long dncDuration = dncEndTime - dncStartTime;

    System.out.println("- Devide and Conquer");
    System.out.printf("  Titik   : %s - %s\n", resultDnC.point1.toString(), resultDnC.point2.toString());
    System.out.printf("  Jarak   : %g\n", resultDnC.distance);
    System.out.printf("  Presisi : %g\n",
        logic.calculateDistance(resultDnC.point1, resultDnC.point2));
    System.out.printf("  Waktu   : %s ns\n", decimalFormat.format(dncDuration));

    System.out.println("\nKesimpulan algoritma:");
    System.out.printf("- Akurasi       : %s\n", resultBf.distance == resultDnC.distance ? "Akurat" : "Tidak akurat");
    System.out.printf("- Selisih waktu : %s ns\n", decimalFormat.format(Math.abs(dncDuration - bfDuration)));
  }

  private static void printPoints(Point[] points) {
    System.out.println("=====================");
    System.out.printf("| %-3s | %-4s | %-4s |\n", "No", "X", "Y");
    System.out.println("---------------------");

    int num = 1;
    for (Point point : points) {
      System.out.printf("| %-3d | %-4d | %-4d |\n", num, point.x, point.y);
      num++;
    }
    System.out.println("=====================");
  }
}

class Logic {
  public double calculateDistance(Point point1, Point point2) {
    // fungsi untuk menghitung jarak antara dua titik
    double diffX = Math.abs(point2.x - point1.x);
    double diffY = Math.abs(point2.y - point1.y);
    return Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));
  }

  public Result bruteForcePoints(Point[] points) {
    // fungsi untuk mencari jarak titik terdekat
    // menggunakan algoritma brute force
    double distance = Double.MAX_VALUE;
    Point point1 = null, point2 = null;
    for (int a = 0; a < points.length - 1; a++) {
      for (int b = a + 1; b < points.length; b++) {
        double newDistance = calculateDistance(points[a], points[b]);
        if (newDistance < distance) {
          distance = newDistance;
          point1 = points[a];
          point2 = points[b];
        }
      }
    }
    return new Result(distance, point1, point2);
  }

  public Result closestPair(Point[] points) {
    // jika array point hanya terdiri dari 1-3 item,
    // maka lakukan perhitungan setiap point
    // menggunakan algoritma brute force
    if (points.length <= 3)
      return bruteForcePoints(points);

    // jika array point > 3, maka lakukan pembagian
    // untuk partisi kiri dan kanan
    int middleIndex = points.length / 2;
    Point[] leftPoints = Arrays.copyOfRange(points, 0, middleIndex);
    Point[] rightPoints = Arrays.copyOfRange(points, middleIndex, points.length);

    Result resultMinPartitionLeft = closestPair(leftPoints);
    Result resultMinPartitionRight = closestPair(rightPoints);

    // hitung nilai minimal dari kedua partisi
    Result resultMin = (resultMinPartitionLeft.distance < resultMinPartitionRight.distance) ? resultMinPartitionLeft
        : resultMinPartitionRight;

    // mencari jika kedua titik berada di partisi yang berbeda
    Point[] middlePoints = new Point[points.length];
    int middlePointIndex = 0;
    for (Point point : points) {
      // selisih antara titik.x dengan titik_tengah.x
      double diff = Math.abs(point.x - points[middleIndex].x);

      // jika selisih < minPartition, artinya
      // titik tersebut berada dekat dengan garis tengah
      // dan < minPartition
      if (diff < resultMin.distance) {
        middlePoints[middlePointIndex] = point;
        middlePointIndex++;
      }
    }

    if (middlePointIndex > 0) {
      // menghitung jarak titik terdekat antar dua partisi
      Point[] middlePoints2 = Arrays.copyOfRange(middlePoints, 0, middlePointIndex);
      for (int a = 0; a < middlePoints2.length - 1; a++) {
        for (int b = a + 1; b < middlePoints2.length; b++) {
          Point point1 = middlePoints2[a];
          Point point2 = middlePoints2[b];
          // jika selisih antara dua titik y < min,
          // maka lakukan perhitungan jarak kedua titik
          double diff = Math.abs(point1.y - point2.y);
          if (diff < resultMin.distance) {

            double distance = calculateDistance(point1, point2);

            if (distance < resultMin.distance)
              resultMin = new Result(distance, point1, point2);

          }
        }
      }
    }

    return resultMin;
  }

  public void generateRandomPoints(Point[] points, int count, int max) {
    // men-generate array point secara random
    // sejumlah `count` dan dalam rentang nilai
    // (0 - `max`)
    Random random = new Random();
    for (int a = 0; a < count; a++)
      points[a] = new Point(random.nextInt(max), random.nextInt(max));
  }

  public void sortPointsByX(Point[] points) {
    // mengurutkan array point berdasarkan nilai x
    // dari kecil ke besar menggunakan algoritma bubble sort
    for (int a = 0; a < points.length - 1; a++) {
      for (int b = 0; b < points.length - 1 - a; b++) {
        if (points[b].x > points[b + 1].x) {
          Point pointTemp = points[b];
          points[b] = points[b + 1];
          points[b + 1] = pointTemp;
        }
      }
    }
  }
}