package kg.touragency.service

import kg.touragency.entity.Tour
import kg.touragency.entity.TourImage
import kg.touragency.repository.TourImageRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import javax.imageio.IIOImage
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

@Service
class TourImageService(private val repo: TourImageRepository) {
    fun save(tour: Tour, file: MultipartFile): TourImage {
        val original = file.bytes

        // Сжимаем картинку, чтобы она занимала меньше места.
        var compressed: ByteArray
        try {
            compressed = compressImage(original)
        } catch (e: Exception) {
            compressed = original
        }

        val image = TourImage(tour = tour, data = compressed, contentType = "image/jpeg")
        return repo.save(image)
    }

    private fun compressImage(input: ByteArray, maxWidth: Int = 1600, quality: Float = 0.85f): ByteArray {
        val bais = ByteArrayInputStream(input)
        val sourceImage = ImageIO.read(bais)

        if (sourceImage == null) {
            return input
        }

        // Если картинка слишком широкая, уменьшаем ее.
        val scale: Double
        if (sourceImage.width > maxWidth) {
            scale = maxWidth.toDouble() / sourceImage.width
        } else {
            scale = 1.0
        }

        val width = Math.max(1, (sourceImage.width * scale).toInt())
        val height = Math.max(1, (sourceImage.height * scale).toInt())
        val smallImage = java.awt.image.BufferedImage(width, height, java.awt.image.BufferedImage.TYPE_INT_RGB)
        val graphics = smallImage.createGraphics()
        graphics.drawImage(sourceImage, 0, 0, width, height, null)
        graphics.dispose()

        val output = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName("jpg")

        // Сохраняем результат как jpg.
        if (writers.hasNext()) {
            val writer = writers.next() as ImageWriter
            val imageOutput = ImageIO.createImageOutputStream(output)
            writer.output = imageOutput

            val param = writer.defaultWriteParam
            if (param.canWriteCompressed()) {
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = quality
            }

            writer.write(null, IIOImage(smallImage, null, null), param)
            imageOutput.close()
            writer.dispose()
            return output.toByteArray()
        } else {
            ImageIO.write(smallImage, "jpg", output)
            return output.toByteArray()
        }
    }

    fun findByTour(tour: Tour): List<TourImage> {
        return repo.findByTourOrderById(tour)
    }

    fun findById(id: Long): TourImage? {
        return repo.findById(id).orElse(null)
    }

    fun deleteById(id: Long) {
        repo.deleteById(id)
    }

    fun recompressAll(): Int {
        val all = repo.findAll()
        var count = 0
        for (img in all) {
            try {
                val original = img.data
                if (original == null) {
                    continue
                }

                var compressed: ByteArray
                try {
                    compressed = compressImage(original)
                } catch (e: Exception) {
                    compressed = original
                }

                img.data = compressed
                img.contentType = "image/jpeg"
                repo.save(img)
                count++
            } catch (e: Exception) {
                // Если одна картинка не сжалась, продолжаем остальные.
            }
        }
        return count
    }
}
