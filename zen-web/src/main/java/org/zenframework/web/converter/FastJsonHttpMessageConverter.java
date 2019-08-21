package org.zenframework.web.converter;

import org.springframework.http.converter.GenericHttpMessageConverter;

/**
 * @author Zeal 2016年5月30日
 * @deprecated 老版本的FastJson是没支持GenericHttpMessageConverter的,现在既然官方实现了就可以不用了
 */
public class FastJsonHttpMessageConverter extends com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter 
    implements GenericHttpMessageConverter<Object> {
//
//	private SerializeFilter[] filters = null;
//
//	static {
//		JSONUtils.defaultSettings();
//	}
//
//    @Override
//    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
//    	throws IOException, HttpMessageNotWritableException {
//
//    	HttpHeaders headers = outputMessage.getHeaders();
//        String text = null;
//        if (this.filters != null && this.filters.length > 0) {
//        	text = JSON.toJSONString(obj, this.filters, this.getFeatures());
//        }
//        else {
//            text = JSON.toJSONString(obj, this.getFeatures());
//        }
//        byte[] bytes = text.getBytes(this.getCharset());
//        headers.setContentLength(bytes.length);
//        OutputStream out = outputMessage.getBody();
//        out.write(bytes);
//    }
//
//	/**
//	 * @return the filters
//	 */
//	public SerializeFilter[] getFilters() {
//		return filters;
//	}
//
//	/**
//	 * @param filters the filters to set
//	 */
//	public void setFilters(SerializeFilter[] filters) {
//		this.filters = filters;
//	}
//
//	@Override
//	public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
//		return this.canRead(contextClass, mediaType);
//	}
//
//	/**
//	 * @param type GenericType
//	 * @param contextClass the ctrl class
//	 */
//	@Override
//	public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage)
//		throws IOException, HttpMessageNotReadableException {
//
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        InputStream in = inputMessage.getBody();
//        byte[] buf = new byte[1024];
//        for (;;) {
//            int len = in.read(buf);
//            if (len == -1) {
//                break;
//            }
//            if (len > 0) {
//                baos.write(buf, 0, len);
//            }
//        }
//
//        byte[] bytes = baos.toByteArray();
//        return JSON.parseObject(bytes, 0, bytes.length, this.getCharset().newDecoder(), type);
//	}
//
//	@Override
//	public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
//		return this.canWrite(clazz, mediaType);
//	}
//
//	@Override
//	public void write(Object t, Type type, MediaType contentType, HttpOutputMessage outputMessage)
//		throws IOException, HttpMessageNotWritableException {
//		this.write(t, contentType, outputMessage);
//	}

}
