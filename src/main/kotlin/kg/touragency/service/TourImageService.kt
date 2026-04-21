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
        val compressed = try { compressImage(original) } catch (e: Exception) { original }
        val img = TourImage(tour = tour, data = compressed, contentType = "image/jpeg")
        return repo.save(img)
    }

    private fun compressImage(input: ByteArray, maxWidth: Int = 1600, quality: Float = 0.85f): ByteArray {
        val bais = ByteArrayInputStream(input)
        val src = ImageIO.read(bais) ?: return input
        val scale = if (src.width > maxWidth) (maxWidth.toDouble() / src.width) else 1.0
        val w = Math.max(1, (src.width * scale).toInt())
        val h = Math.max(1, (src.height * scale).toInt())
        val thumb = java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB)
        val g = thumb.createGraphics()
        g.drawImage(src, 0, 0, w, h, null)
        g.dispose()

        val baos = ByteArrayOutputStream()
        val writers = ImageIO.getImageWritersByFormatName("jpg")
        if (writers.hasNext()) {
            val writer = writers.next() as ImageWriter
            val ios = ImageIO.createImageOutputStream(baos)
            writer.output = ios
            val param = writer.defaultWriteParam
            if (param.canWriteCompressed()) {
                param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                param.compressionQuality = quality
            }
            writer.write(null, IIOImage(thumb, null, null), param)
            ios.close()
            writer.dispose()
            return baos.toByteArray()
        } else {
            ImageIO.write(thumb, "jpg", baos)
            return baos.toByteArray()
        }
    }

    fun findByTour(tour: Tour): List<TourImage> = repo.findByTourOrderById(tour)
    fun findById(id: Long): TourImage? = repo.findById(id).orElse(null)
    fun deleteById(id: Long) = repo.deleteById(id)

    fun recompressAll(): Int {
        val all = repo.findAll()
        var count = 0
        for (img in all) {
            try {
                val original = img.data ?: continue
                val compressed = try { compressImage(original) } catch (e: Exception) { original }
                img.data = compressed
                img.contentType = "image/jpeg"
                repo.save(img)
                count++
            } catch (e: Exception) {
                // ignore individual failures
            }
        }
        return count
    }
}
